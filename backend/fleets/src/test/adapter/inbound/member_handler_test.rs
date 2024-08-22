#[cfg(test)]
mod tests {
    use std::sync::Arc;

    use axum::Router;
    use axum_test::TestServer;
    use serde_json::json;

    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use shared::infra::time::FakeTimeProvider;
    use shared::test::fake::in_memory_sns_client::InMemorySNSClient;
    use shared::test::fake::in_memory_user_http_client::InMemoryUserHttpClient;

    use crate::adapter::inbound::member_handler::member_router;
    use crate::domain::port::fleet_repository::FleetRepository;
    use crate::domain::service::member_invitation_dispatcher_impl::MemberInvitationDispatcherImpl;
    use crate::test::fake::in_memory_fleet_repository::InMemoryFleetRepository;
    use crate::{MemberHandlerState, SNSTopicArns};

    type TimeProviderArc = Arc<FakeTimeProvider>;
    type UserHttpClientArc = Arc<InMemoryUserHttpClient<TimeProviderArc>>;

    type FleetRepositoryArc = Arc<InMemoryFleetRepository>;

    struct TestDependencies {
        user_http_client: UserHttpClientArc,
        fleet_repository: FleetRepositoryArc,
        router: Router,
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

        // Router
        let router = member_router().with_state(MemberHandlerState {
            invitation_dispatcher: invitation_dispatcher.clone(),
        });

        TestDependencies {
            user_http_client,
            fleet_repository,
            router,
        }
    }

    #[tokio::test]
    async fn should_return_ok_when_inviting_new_member() {
        // given
        let TestDependencies {
            fleet_repository,
            router,
            ..
        } = setup();
        let client = TestServer::new(router).unwrap();

        let role = Admin;
        let fleet_name = "fleet_name".to_string();
        let first_name = "first_name".to_string();
        let last_name = "last_name".to_string();
        let email = "email@gmail.com".to_string();

        // when
        let fleet_id = fleet_repository.insert(fleet_name).await.unwrap();

        let body = json!({
            "role": role,
            "fleet_id": fleet_id,
            "first_name": first_name,
            "last_name": last_name,
            "email": email
        });

        // when
        let response = client.post("/members/invite").json(&body).await;

        // then
        response.assert_status_ok();
    }

    #[tokio::test]
    async fn should_return_bad_request_when_inviting_member_to_non_existing_fleet() {
        // given
        let TestDependencies { router, .. } = setup();
        let client = TestServer::new(router).unwrap();

        let role = Admin;
        let fleet_id = FleetId::default();
        let first_name = "first_name".to_string();
        let last_name = "last_name".to_string();
        let email = "email@gmail.com".to_string();

        // when
        let body = json!({
            "role": role,
            "fleet_id": fleet_id,
            "first_name": first_name,
            "last_name": last_name,
            "email": email
        });

        let response = client.post("/members/invite").json(&body).await;

        // then
        response.assert_status_bad_request();
    }

    #[tokio::test]
    async fn should_return_bad_request_when_inviting_member_that_already_exists() {
        // given
        let TestDependencies {
            fleet_repository,
            user_http_client,
            router,
            ..
        } = setup();
        let client = TestServer::new(router).unwrap();

        let role = Admin;
        let fleet_name = "fleet_name".to_string();
        let first_name = "first_name".to_string();
        let last_name = "last_name".to_string();
        let email = "email@gmail.com".to_string();

        // when
        let fleet_id = fleet_repository.insert(fleet_name).await.unwrap();
        user_http_client.insert_user(email.clone());

        let body = json!({
            "role": role,
            "fleet_id": fleet_id,
            "first_name": first_name,
            "last_name": last_name,
            "email": email
        });

        let response = client.post("/members/invite").json(&body).await;

        // then
        response.assert_status_bad_request();
    }
}
