#[cfg(test)]
mod tests {
    use serde_json::to_string;
    use shared::domain::event::users::user_invited;
    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use shared::infra::time::FakeTimeProvider;
    use shared::test::fake::in_memory_sns_client::InMemorySNSClient;
    use shared::test::fake::in_memory_user_http_client::InMemoryUserHttpClient;
    use std::sync::Arc;

    use crate::domain::error::MemberInvitationError::{FleetNotExists, MemberAlreadyExists};
    use crate::domain::port::fleet_repository::FleetRepository;
    use crate::domain::port::member_invitation_dispatcher::MemberInvitationDispatcher;
    use crate::domain::service::member_invitation_dispatcher_impl::MemberInvitationDispatcherImpl;
    use crate::test::fake::in_memory_fleet_repository::InMemoryFleetRepository;
    use crate::SNSTopicArns;

    type TimeProviderArc = Arc<FakeTimeProvider>;

    type SNSClientArc = Arc<InMemorySNSClient>;
    type UserHttpClientArc = Arc<InMemoryUserHttpClient<TimeProviderArc>>;

    type FleetRepositoryArc = Arc<InMemoryFleetRepository>;

    type MemberInvitationDispatcherArc =
        Arc<MemberInvitationDispatcherImpl<TimeProviderArc, SNSClientArc, UserHttpClientArc, FleetRepositoryArc>>;

    struct TestDependencies {
        time_provider: TimeProviderArc,
        sns_client: SNSClientArc,
        user_http_client: UserHttpClientArc,
        fleet_repository: FleetRepositoryArc,
        invitation_dispatcher: MemberInvitationDispatcherArc,
    }

    fn setup() -> TestDependencies {
        let time_provider = Arc::new(FakeTimeProvider::new());

        // Clients
        let sns_client = Arc::new(InMemorySNSClient::new());
        let user_http_client = Arc::new(InMemoryUserHttpClient::new(time_provider.clone()));

        // Repositories
        let fleet_repository = Arc::new(InMemoryFleetRepository::new());

        // Services
        let invitation_dispatcher = Arc::new(MemberInvitationDispatcherImpl::new(
            SNSTopicArns::default(),
            time_provider.clone(),
            sns_client.clone(),
            user_http_client.clone(),
            fleet_repository.clone(),
        ));

        TestDependencies {
            time_provider,
            sns_client,
            user_http_client,
            fleet_repository,
            invitation_dispatcher,
        }
    }

    #[tokio::test]
    async fn should_invite_new_member() {
        // given
        let TestDependencies {
            time_provider,
            sns_client,
            fleet_repository,
            invitation_dispatcher,
            ..
        } = setup();

        let role = Admin;
        let fleet_name = "fleet_name".to_string();
        let first_name = "first_name".to_string();
        let last_name = "last_name".to_string();
        let email = "email@gmail.com".to_string();

        // when
        let fleet_id = fleet_repository.insert(fleet_name).await.unwrap();

        invitation_dispatcher
            .invite_member(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
            )
            .await
            .unwrap();

        // then
        let expected = user_invited(&time_provider)(
            fleet_id.clone(),
            role.clone(),
            first_name.clone(),
            last_name.clone(),
            email.clone(),
        );
        let messages = sns_client.get_messages();

        assert_eq!(messages.len(), 1);
        assert_eq!(messages[0].message, to_string(&expected).unwrap());
    }

    #[tokio::test]
    async fn should_return_error_if_fleet_doesnt_exist() {
        // given
        let TestDependencies {
            sns_client,
            invitation_dispatcher,
            ..
        } = setup();

        let role = Admin;
        let fleet_id = FleetId::default();
        let first_name = "first_name".to_string();
        let last_name = "last_name".to_string();
        let email = "email@gmail.com".to_string();

        // when
        let result = invitation_dispatcher
            .invite_member(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
            )
            .await
            .unwrap_err();

        // then
        let messages = sns_client.get_messages();

        assert_eq!(messages.len(), 0);
        assert!(matches!(result, FleetNotExists));
    }

    #[tokio::test]
    async fn should_return_error_if_member_already_exists() {
        // given
        let TestDependencies {
            sns_client,
            user_http_client,
            fleet_repository,
            invitation_dispatcher,
            ..
        } = setup();

        let role = Admin;
        let fleet_name = "fleet_name".to_string();
        let first_name = "first_name".to_string();
        let last_name = "last_name".to_string();
        let email = "email@gmail.com".to_string();

        // when
        let fleet_id = fleet_repository.insert(fleet_name).await.unwrap();
        user_http_client.insert_user(email.clone());

        let result = invitation_dispatcher
            .invite_member(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
            )
            .await
            .unwrap_err();

        // then
        let messages = sns_client.get_messages();

        assert_eq!(messages.len(), 0);
        assert!(matches!(result, MemberAlreadyExists));
    }
}
