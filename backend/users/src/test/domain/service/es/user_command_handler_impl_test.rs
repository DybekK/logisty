#[cfg(test)]
mod tests {
    use crate::adapter::es::user_command_handler_impl::UserCommandHandlerImpl;
    use crate::domain::error::InvitationError::{InvitationInactive, InvitationNotFound};
    use crate::domain::error::UserError::InvitationError;
    use crate::domain::port::invitation_service::InvitationService;
    use crate::domain::port::user_command_handler::UserCommandHandler;
    use crate::domain::service::invitation_service_impl::InvitationServiceImpl;
    use crate::test::fake::in_memory_invitation_repository::InMemoryInvitationRepository;
    use crate::SNSTopicArns;
    use chrono::Duration;
    use serde_json::to_string;
    use shared::domain::event::users::user_registered;
    use shared::domain::types::id::{FleetId, InvitationId};
    use shared::domain::types::Role::Admin;
    use shared::infra::time::{FakeTimeProvider, TimeProvider};
    use shared::test::fake::in_memory_sns_client::InMemorySNSClient;
    use std::sync::Arc;

    type TimeProviderArc = Arc<FakeTimeProvider>;

    type SNSClientArc = Arc<InMemorySNSClient>;

    type InvitationRepositoryArc = Arc<InMemoryInvitationRepository>;

    type InvitationServiceArc = Arc<InvitationServiceImpl<TimeProviderArc, InvitationRepositoryArc>>;

    type UserCommandHandlerArc = Arc<UserCommandHandlerImpl<TimeProviderArc, SNSClientArc, InvitationServiceArc>>;

    struct TestDependencies {
        time_provider: TimeProviderArc,
        sns_client: SNSClientArc,
        invitation_service: InvitationServiceArc,
        command_handler: UserCommandHandlerArc,
    }

    fn setup() -> TestDependencies {
        let time_provider = Arc::new(FakeTimeProvider::new());

        // Clients
        let sns_client = Arc::new(InMemorySNSClient::new());

        // Repositories
        let invitation_repository = Arc::new(InMemoryInvitationRepository::new());

        // Services
        let invitation_service = Arc::new(InvitationServiceImpl::new(time_provider.clone(), invitation_repository));

        let command_handler = Arc::new(UserCommandHandlerImpl::new(
            SNSTopicArns::default(),
            time_provider.clone(),
            sns_client.clone(),
            invitation_service.clone(),
        ));

        TestDependencies {
            time_provider,
            sns_client,
            invitation_service,
            command_handler,
        }
    }

    #[tokio::test]
    async fn should_register_user() {
        // given
        let TestDependencies {
            time_provider,
            sns_client,
            invitation_service,
            command_handler,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@gmail.com".to_string();
        let password = "password".to_string();
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

        // when
        command_handler
            .handle_register_user(invitation_id.clone(), password.clone())
            .await
            .unwrap();

        // then
        let expected = user_registered(&time_provider)(invitation_id, fleet_id, role, first_name, last_name, password, email);
        let messages = sns_client.get_messages();

        assert_eq!(messages.len(), 1);
        assert_eq!(messages[0].message, to_string(&expected).unwrap());
    }

    #[tokio::test]
    async fn should_return_error_if_invitation_not_exists() {
        // given
        let TestDependencies {
            sns_client,
            command_handler,
            ..
        } = setup();
        let password = "password".to_string();

        // when
        let invitation_id = InvitationId::default();
        let result = command_handler
            .handle_register_user(invitation_id, password.clone())
            .await
            .unwrap_err();

        // then
        let messages = sns_client.get_messages();

        assert_eq!(messages.len(), 0);
        assert!(matches!(result, InvitationError(InvitationNotFound)));
    }

    #[tokio::test]
    async fn should_return_error_if_invitation_is_not_active() {
        // given
        let TestDependencies {
            time_provider,
            sns_client,
            invitation_service,
            command_handler,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@gmail.com".to_string();
        let password = "password".to_string();
        let created_at = time_provider.now();

        // when
        let invitation_id = invitation_service
            .create_invitation(fleet_id, role, first_name, last_name, email, created_at)
            .await
            .unwrap();

        time_provider.advance(Duration::days(8));

        // when
        let result = command_handler
            .handle_register_user(invitation_id, password.clone())
            .await
            .unwrap_err();

        // then
        let messages = sns_client.get_messages();

        assert_eq!(messages.len(), 0);
        assert!(matches!(result, InvitationError(InvitationInactive)));
    }
}
