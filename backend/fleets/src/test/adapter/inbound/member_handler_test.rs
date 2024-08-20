#[cfg(test)]
mod tests {
    use std::sync::Arc;

    use axum::Router;
    use axum_test::TestServer;
    use serde_json::json;

    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use shared::test::fake::in_memory_sns_client::InMemorySNSClient;
    use shared::test::fake::in_memory_user_http_client::InMemoryUserHttpClient;

    use crate::adapter::inbound::member_handler::member_router;
    use crate::domain::port::fleet_repository::FleetRepository;
    use crate::domain::service::member_invitation_dispatcher_impl::MemberInvitationDispatcherImpl;
    use crate::test::fake::in_memory_fleet_repository::InMemoryFleetRepository;
    use crate::MemberHandlerState;

    type SNSClientArc = Arc<InMemorySNSClient>;
    type UserHttpClientArc = Arc<InMemoryUserHttpClient>;
    type FleetRepositoryArc = Arc<InMemoryFleetRepository>;
    type MemberInvitationDispatcherArc = Arc<MemberInvitationDispatcherImpl<SNSClientArc, UserHttpClientArc, FleetRepositoryArc>>;

    fn setup() -> (
        SNSClientArc,
        UserHttpClientArc,
        FleetRepositoryArc,
        MemberInvitationDispatcherArc,
        Router,
    ) {
        let sns_client = Arc::new(InMemorySNSClient::new());
        let user_http_client = Arc::new(InMemoryUserHttpClient::new());
        let fleet_repository = Arc::new(InMemoryFleetRepository::new());

        let invitation_dispatcher = Arc::new(MemberInvitationDispatcherImpl::new(
            sns_client.clone(),
            user_http_client.clone(),
            fleet_repository.clone(),
        ));

        let router = member_router().with_state(MemberHandlerState {
            invitation_dispatcher: invitation_dispatcher.clone(),
        });

        (sns_client, user_http_client, fleet_repository, invitation_dispatcher, router)
    }

    #[tokio::test]
    async fn should_return_ok_when_inviting_new_member() {
        // given
        let (_, _, fleet_repository, _, router) = setup();
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
        let (_, _, _, _, router) = setup();
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
        let (_, user_http_client, fleet_repository, _, router) = setup();
        let client = TestServer::new(router).unwrap();

        let role = Admin;
        let fleet_name = "fleet_name".to_string();
        let first_name = "first_name".to_string();
        let last_name = "last_name".to_string();
        let email = "email@gmail.com".to_string();

        // when
        let fleet_id = fleet_repository.insert(fleet_name).await.unwrap();
        user_http_client.insert(email.clone());

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
