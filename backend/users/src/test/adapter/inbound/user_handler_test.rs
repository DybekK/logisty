#[cfg(test)]
mod tests {
    use std::sync::Arc;

    use crate::adapter::es::user_command_handler_impl::UserCommandHandlerImpl;
    use crate::adapter::inbound::user_handler::user_router;
    use crate::domain::port::invitation_service::InvitationService;
    use crate::domain::port::user_repository::UserRepository;
    use crate::domain::port::user_service::UserService;
    use crate::domain::service::invitation_service_impl::InvitationServiceImpl;
    use crate::domain::service::user_service_impl::UserServiceImpl;
    use crate::test::fake::in_memory_invitation_repository::InMemoryInvitationRepository;
    use crate::test::fake::in_memory_user_repository::InMemoryUserRepository;
    use crate::{SNSTopicArns, UserHandlerState};
    use axum::Router;
    use axum_test::TestServer;
    use chrono::Duration;
    use serde_json::json;
    use shared::domain::types::id::{FleetId, InvitationId, UserId};
    use shared::domain::types::Role::Admin;
    use shared::infra::time::{FakeTimeProvider, TimeProvider};
    use shared::test::fake::in_memory_sns_client::InMemorySNSClient;

    type TimeProviderArc = Arc<FakeTimeProvider>;

    type UserRepositoryArc = Arc<InMemoryUserRepository>;
    type InvitationRepositoryArc = Arc<InMemoryInvitationRepository>;

    type InvitationServiceArc = Arc<InvitationServiceImpl<TimeProviderArc, InvitationRepositoryArc>>;
    type UserServiceArc = Arc<UserServiceImpl<UserRepositoryArc>>;

    struct TestDependencies {
        pub time_provider: TimeProviderArc,

        pub user_repository: UserRepositoryArc,

        pub invitation_service: InvitationServiceArc,
        pub user_service: UserServiceArc,

        pub router: Router,
    }

    fn setup() -> TestDependencies {
        let time_provider = Arc::new(FakeTimeProvider::new());

        // Clients
        let default_sns_client = Arc::new(InMemorySNSClient::new());

        // Repositories
        let user_repository = Arc::new(InMemoryUserRepository::new());
        let invitation_repository = Arc::new(InMemoryInvitationRepository::new());

        // Services
        let invitation_service = Arc::new(InvitationServiceImpl::new(
            time_provider.clone(),
            invitation_repository.clone(),
        ));
        let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

        // Command handlers
        let user_command_handler = UserCommandHandlerImpl::new(
            SNSTopicArns::default(),
            time_provider.clone(),
            default_sns_client.clone(),
            invitation_service.clone(),
        );

        // Router
        let router = user_router().with_state(UserHandlerState {
            command_handler: user_command_handler.clone(),
            user_service: user_service.clone(),
        });

        TestDependencies {
            time_provider,
            user_repository,
            invitation_service,
            user_service,
            router,
        }
    }

    // get_user_by_handler

    #[tokio::test]
    async fn should_return_ok_when_user_found_by_id() {
        // given
        let TestDependencies {
            time_provider,
            user_service,
            router,
            ..
        } = setup();
        let client = TestServer::new(router).unwrap();

        // when
        let fleet_id = FleetId::default();
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@gmail.com".to_string();
        let password = "password".to_string();
        let role = Admin;
        let created_at = time_provider.now();

        let user_id = user_service
            .register_user(fleet_id, first_name, last_name, email, password, role, created_at)
            .await
            .unwrap();

        // when
        let response = client.get("/users").add_query_param("user_id", user_id).await;

        // then
        response.assert_status_ok();
    }

    #[tokio::test]
    async fn should_return_ok_when_user_found_by_email() {
        // given
        let TestDependencies {
            time_provider,
            user_repository,
            router,
            ..
        } = setup();
        let client = TestServer::new(router).unwrap();

        // when
        let fleet_id = FleetId::default();
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@gmail.com".to_string();
        let password = "password".to_string();
        let role = Admin;
        let created_at = time_provider.now();

        let _ = user_repository
            .insert(
                fleet_id.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                password.clone(),
                role.clone(),
                created_at.clone(),
            )
            .await
            .unwrap();

        // when
        let response = client.get("/users").add_query_param("email", email).await;

        // then
        response.assert_status_ok();
    }

    #[tokio::test]
    async fn should_return_bad_request_when_invalid_search_criteria() {
        // given
        let TestDependencies { router, .. } = setup();
        let client = TestServer::new(router).unwrap();

        // when
        let user_id = UserId::default();
        let email = "email@gmail.com".to_string();

        let response = client
            .get("/users")
            .add_query_param("user_id", user_id)
            .add_query_param("email", email)
            .await;

        // then
        response.assert_status_bad_request();
    }

    // register_user_handler

    #[tokio::test]
    async fn should_return_ok_when_user_registered() {
        // given
        let TestDependencies {
            time_provider,
            invitation_service,
            router,
            ..
        } = setup();
        let client = TestServer::new(router).unwrap();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@example.com".to_string();
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

        let body = json!({ "invitation_id": invitation_id, "password": password });

        // when
        let response = client.post("/users/register").json(&body).await;

        // then
        response.assert_status_ok();
    }

    #[tokio::test]
    async fn should_return_bad_request_when_invitation_not_exists() {
        // given
        let TestDependencies { router, .. } = setup();
        let client = TestServer::new(router).unwrap();

        let password = "password".to_string();

        // when
        let invitation_id = InvitationId::default();
        let body = json!({ "invitation_id": invitation_id, "password": password });

        // when
        let response = client.post("/users/register").json(&body).await;

        // then
        response.assert_status_bad_request();
    }

    #[tokio::test]
    async fn should_return_bad_request_when_is_not_active() {
        // given
        let TestDependencies {
            time_provider,
            invitation_service,
            router,
            ..
        } = setup();
        let client = TestServer::new(router).unwrap();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@example.com".to_string();
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

        let body = json!({ "invitation_id": invitation_id, "password": password });

        time_provider.advance(Duration::days(8));

        // when
        let response = client.post("/users/register").json(&body).await;

        // then
        response.assert_status_bad_request();
    }
}
