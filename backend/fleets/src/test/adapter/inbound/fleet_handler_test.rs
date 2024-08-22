#[cfg(test)]
mod tests {
    use std::sync::Arc;

    use crate::adapter::inbound::fleet_handler::fleet_router;
    use crate::domain::service::fleet_service_impl::FleetServiceImpl;
    use crate::test::fake::in_memory_fleet_repository::InMemoryFleetRepository;
    use crate::FleetHandlerState;
    use axum::Router;
    use axum_test::TestServer;
    use serde_json::json;

    struct TestDependencies {
        router: Router,
    }

    fn setup() -> TestDependencies {
        let fleet_repository = Arc::new(InMemoryFleetRepository::new());
        let fleet_service = Arc::new(FleetServiceImpl::new(fleet_repository.clone()));

        let router = fleet_router().with_state(FleetHandlerState {
            fleet_service: fleet_service.clone(),
        });

        TestDependencies { router }
    }

    #[tokio::test]
    async fn should_return_ok_when_creating_fleet() {
        // given
        let TestDependencies { router, .. } = setup();

        let client = TestServer::new(router).unwrap();
        let body = json!({ "fleet_name": "new_fleet" });

        // when
        let response = client.post("/fleets/create").json(&body).await;

        // then
        response.assert_status_ok();
    }

    #[tokio::test]
    async fn should_return_bad_request_when_creating_fleet_that_already_exists() {
        // given
        let TestDependencies { router, .. } = setup();

        let client = TestServer::new(router).unwrap();
        let body = json!({ "fleet_name": "new_fleet" });

        // when
        let _ = client.post("/fleets/create").json(&body).await;
        let response = client.post("/fleets/create").json(&body).await;

        // then
        response.assert_status_bad_request();
    }
}
