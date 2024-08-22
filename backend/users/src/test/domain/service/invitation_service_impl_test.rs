#[cfg(test)]
mod tests {
    use crate::domain::port::invitation_repository::InvitationRepository;
    use crate::domain::port::invitation_service::InvitationService;
    use crate::domain::service::invitation_service_impl::InvitationServiceImpl;
    use crate::test::fake::in_memory_invitation_repository::InMemoryInvitationRepository;

    use crate::domain::error::InvitationError::InvitationInactive;
    use crate::domain::model::Invitation;
    use chrono::{Duration, NaiveDateTime};
    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use shared::infra::time::{FakeTimeProvider, TimeProvider};
    use std::sync::Arc;

    type TimeProviderArc = Arc<FakeTimeProvider>;

    type InvitationRepositoryArc = Arc<InMemoryInvitationRepository>;

    type InvitationServiceArc = Arc<InvitationServiceImpl<TimeProviderArc, InvitationRepositoryArc>>;

    struct TestDependencies {
        pub time_provider: TimeProviderArc,

        pub invitation_repository: InvitationRepositoryArc,

        pub invitation_service: InvitationServiceArc,
    }

    fn setup() -> TestDependencies {
        let time_provider = Arc::new(FakeTimeProvider::new());

        // Repositories
        let invitation_repository = Arc::new(InMemoryInvitationRepository::new());

        // Services
        let invitation_service = Arc::new(InvitationServiceImpl::new(
            time_provider.clone(),
            invitation_repository.clone(),
        ));

        TestDependencies {
            time_provider,
            invitation_repository,
            invitation_service,
        }
    }

    // get invitation by

    #[tokio::test]
    async fn should_find_invitation_by() {
        // given
        let TestDependencies {
            time_provider,
            invitation_service,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@gmail.com".to_string();
        let role = Admin;
        let created_at = time_provider.now();

        let invitation_id = invitation_service
            .create_invitation(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                created_at,
            )
            .await
            .unwrap();

        // when
        let invitation = invitation_service
            .get_invitation_by(Some(email.clone()))
            .await
            .unwrap()
            .unwrap();

        // then
        let expected_invitation = Invitation {
            invitation_id,
            email,
            fleet_id,
            role,
            first_name,
            last_name,
            due_at: time_provider.now() + Duration::days(7),
            created_at: time_provider.now(),
            accepted_at: None,
        };

        assert_eq!(invitation, expected_invitation);
    }

    // create invitation

    #[tokio::test]
    async fn should_create_invitation() {
        // given
        let TestDependencies {
            time_provider,
            invitation_repository,
            invitation_service,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@example.com".to_string();
        let created_at = time_provider.now();

        // when
        let invitation_id = invitation_service
            .create_invitation(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                created_at,
            )
            .await
            .unwrap();

        let invitation = invitation_repository
            .find_by_id(invitation_id.clone())
            .await
            .unwrap()
            .unwrap();

        // then
        let expected_invitation = Invitation {
            invitation_id,
            email,
            fleet_id,
            role,
            first_name,
            last_name,
            due_at: NaiveDateTime::default() + Duration::days(7),
            created_at: NaiveDateTime::default(),
            accepted_at: None,
        };

        assert_eq!(invitation, expected_invitation);
    }

    #[tokio::test]
    async fn should_get_active_invitation() {
        // given
        let TestDependencies {
            time_provider,
            invitation_service,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@example.com".to_string();
        let created_at = time_provider.now();

        // when
        let invitation_id = invitation_service
            .create_invitation(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                created_at,
            )
            .await
            .unwrap();

        let invitation = invitation_service.get_active_invitation(invitation_id.clone()).await.unwrap();

        // then
        let expected_invitation = Invitation {
            invitation_id,
            first_name,
            last_name,
            email,
            fleet_id,
            role,
            due_at: NaiveDateTime::default() + Duration::days(7),
            created_at: NaiveDateTime::default(),
            accepted_at: None,
        };

        assert_eq!(invitation, expected_invitation);
    }

    #[tokio::test]
    async fn should_return_error_for_not_active_invitation_due_to_expiration() {
        // given
        let TestDependencies {
            time_provider,
            invitation_service,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@example.com".to_string();
        let created_at = time_provider.now();

        // when
        let invitation_id = invitation_service
            .create_invitation(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                created_at,
            )
            .await
            .unwrap();

        time_provider.advance(Duration::days(8));

        let error = invitation_service
            .get_active_invitation(invitation_id.clone())
            .await
            .unwrap_err();

        // then
        assert!(matches!(error, InvitationInactive));
    }

    // accept invitation

    #[tokio::test]
    async fn should_accept_invitation() {
        // given
        let TestDependencies {
            time_provider,
            invitation_repository,
            invitation_service,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@gmail.com".to_string();
        let created_at = time_provider.now();

        // when
        let invitation_id = invitation_service
            .create_invitation(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                created_at,
            )
            .await
            .unwrap();

        invitation_service
            .accept_invitation(invitation_id.clone(), created_at)
            .await
            .unwrap();

        // then
        let invitation = invitation_repository
            .find_by_id(invitation_id.clone())
            .await
            .unwrap()
            .unwrap();

        assert!(invitation.accepted_at.is_some());
    }
}
