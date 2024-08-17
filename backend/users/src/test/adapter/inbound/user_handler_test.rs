#[cfg(test)]
mod tests {
    use std::sync::Arc;

    use axum::Router;
    use axum_test::TestServer;

    use shared::domain::types::id::UserId;

    use crate::adapter::inbound::user_handler::user_router;
    use crate::domain::model::Role::Admin;
    use crate::domain::port::user_repository::UserRepository;
    use crate::domain::service::user_service_impl::UserServiceImpl;
    use crate::test::fake::in_memory_user_repository::InMemoryUserRepository;
    use crate::UserHandlerState;

    type UserRepositoryArc = Arc<InMemoryUserRepository>;
    type UserServiceArc = Arc<UserServiceImpl<UserRepositoryArc>>;

    fn setup() -> (UserRepositoryArc, UserServiceArc, Router) {
        let user_repository = Arc::new(InMemoryUserRepository::new());
        let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

        let router = user_router().with_state(UserHandlerState {
            user_service: user_service.clone(),
        });

        (user_repository, user_service, router)
    }

    #[tokio::test]
    async fn should_return_ok_when_user_found_by_id() {
        // given
        let (user_repository, _, router) = setup();
        let client = TestServer::new(router).unwrap();

        // when
        let email = "email@gmail.com".to_string();
        let password = "password".to_string();
        let role = Admin;

        let user_id = user_repository.insert(email, password, role).await.unwrap();

        // when
        let response = client.get("/users").add_query_param("user_id", user_id).await;

        // then
        response.assert_status_ok();
    }

    #[tokio::test]
    async fn should_return_ok_when_user_found_by_email() {
        // given
        let (user_repository, _, router) = setup();
        let client = TestServer::new(router).unwrap();

        // when
        let email = "email@gmail.com".to_string();
        let password = "password".to_string();
        let role = Admin;

        let _ = user_repository.insert(email.clone(), password, role).await.unwrap();

        // when
        let response = client.get("/users").add_query_param("email", email).await;

        // then
        response.assert_status_ok();
    }

    #[tokio::test]
    async fn should_return_bad_request_when_invalid_search_criteria() {
        // given
        let (_, _, router) = setup();
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
}
