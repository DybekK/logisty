#[cfg(test)]
mod tests {
    use crate::test::fake::in_memory_fleet_repository::InMemoryFleetRepository;
    use chrono::NaiveDateTime;
    use std::sync::Arc;

    use crate::domain::error::FleetError::FleetAlreadyExists;
    use crate::domain::model::Fleet;
    use crate::domain::port::fleet_repository::FleetRepository;
    use crate::domain::port::fleet_service::FleetService;
    use crate::domain::service::fleet_service_impl::FleetServiceImpl;

    type FleetRepositoryArc = Arc<InMemoryFleetRepository>;
    type FleetServiceArc = Arc<FleetServiceImpl<FleetRepositoryArc>>;

    struct TestDependencies {
        fleet_repository: FleetRepositoryArc,
        fleet_service: FleetServiceArc,
    }

    fn setup() -> TestDependencies {
        // Repositories
        let fleet_repository = Arc::new(InMemoryFleetRepository::new());

        // Services
        let fleet_service = Arc::new(FleetServiceImpl::new(fleet_repository.clone()));

        TestDependencies {
            fleet_repository,
            fleet_service,
        }
    }

    #[tokio::test]
    async fn should_create_new_fleet_successfully() {
        // given
        let TestDependencies {
            fleet_repository,
            fleet_service,
        } = setup();

        let fleet_name = "fleet_name".to_string();

        // when
        let fleet_id = fleet_service.create_new_fleet(fleet_name.clone()).await.unwrap();
        let fleet = fleet_repository.find_by_id(fleet_id.clone()).await.unwrap().unwrap();

        // then
        let expected_fleet = Fleet {
            fleet_id: fleet_id.clone(),
            fleet_name: fleet_name.clone(),
            created_at: NaiveDateTime::default(),
            updated_at: NaiveDateTime::default(),
        };

        assert_eq!(fleet, expected_fleet);
    }

    #[tokio::test]
    async fn should_not_allow_duplicate_fleet_names() {
        // given
        let TestDependencies { fleet_service, .. } = setup();

        let fleet_name = "fleet_name".to_string();

        // when
        let _ = fleet_service.create_new_fleet(fleet_name.clone()).await.unwrap();
        let result = fleet_service.create_new_fleet(fleet_name.clone()).await.unwrap_err();

        // then
        assert!(matches!(result, FleetAlreadyExists));
    }
}
