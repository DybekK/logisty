#[cfg(test)]
mod tests {
    use crate::adapter::es::user_projection_handler_impl::UserProjectionHandlerImpl;
    use crate::domain::port::invitation_service::InvitationService;
    use crate::domain::port::user_projection_handler::UserProjectionHandler;
    use crate::domain::service::invitation_service_impl::InvitationServiceImpl;
    use crate::domain::service::user_service_impl::UserServiceImpl;
    use crate::test::fake::in_memory_invitation_repository::InMemoryInvitationRepository;
    use crate::test::fake::in_memory_user_repository::InMemoryUserRepository;
    use shared::domain::event::users::UserRegisteredPayload;
    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use shared::infra::time::{FakeTimeProvider, TimeProvider};
    use std::sync::Arc;

    type TimeProviderArc = Arc<FakeTimeProvider>;

    type UserRepositoryArc = Arc<InMemoryUserRepository>;
    type InvitationRepositoryArc = Arc<InMemoryInvitationRepository>;

    type InvitationServiceArc = Arc<InvitationServiceImpl<TimeProviderArc, InvitationRepositoryArc>>;
    type UserServiceArc = Arc<UserServiceImpl<UserRepositoryArc>>;

    type UserProjectionHandlerArc = Arc<UserProjectionHandlerImpl<UserServiceArc, InvitationServiceArc>>;

    struct TestDependencies {
        pub time_provider: TimeProviderArc,

        pub invitation_service: InvitationServiceArc,

        pub handler: UserProjectionHandlerArc,
    }

    fn setup() -> TestDependencies {
        let time_provider = Arc::new(FakeTimeProvider::new());
        // Repositories
        let user_repository = Arc::new(InMemoryUserRepository::new());
        let invitation_repository = Arc::new(InMemoryInvitationRepository::new());

        // Services
        let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));
        let invitation_service = Arc::new(InvitationServiceImpl::new(
            time_provider.clone(),
            invitation_repository.clone(),
        ));

        // Handler
        let handler = Arc::new(UserProjectionHandlerImpl::new(
            user_service.clone(),
            invitation_service.clone(),
        ));

        TestDependencies {
            time_provider,
            invitation_service,
            handler,
        }
    }

    #[tokio::test]
    async fn should_project_event() {
        // given
        let TestDependencies {
            time_provider,
            invitation_service,
            handler,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@example.com".to_string();
        let password = "password".to_string();
        let created_at = time_provider.now();
        let accepted_at = time_provider.now();

        // when
        let invitation_id = invitation_service
            .create_invitation(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                created_at.clone(),
            )
            .await
            .unwrap();

        let payload = UserRegisteredPayload {
            invitation_id,
            fleet_id,
            role,
            first_name,
            last_name,
            email,
            password,
            accepted_at,
        };

        let result = handler.handle_registered_user(payload).await;

        // then
        assert!(result.is_ok());
    }
}
