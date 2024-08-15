#[cfg(test)]
mod tests {
    use std::sync::Arc;

    use crate::test::fake::in_memory_fleet_repository::InMemoryFleetRepository;
    
    use crate::domain::service::fleet_service_impl::FleetServiceImpl;
    use crate::domain::port::fleet_repository::FleetRepository;
    use crate::domain::port::fleet_service::FleetService;
    use crate::domain::error::FleetError::FleetAlreadyExists;

    type FleetRepositoryArc = Arc<InMemoryFleetRepository>;
    type FleetServiceArc = Arc<FleetServiceImpl<FleetRepositoryArc>>;

    fn setup() -> (FleetRepositoryArc, FleetServiceArc) {
        let fleet_repository = Arc::new(InMemoryFleetRepository::new());
        let fleet_service = Arc::new(FleetServiceImpl::new(fleet_repository.clone()));

        (fleet_repository, fleet_service)
    }

    #[tokio::test]
    async fn should_create_new_fleet_successfully() {
        // given
        let (fleet_repository, fleet_service) = setup();

        let fleet_name = "fleet_name".to_string();

        // when
        let fleet_id = fleet_service.create_new_fleet(fleet_name.clone()).await.unwrap();
        let fleet = fleet_repository.find_by_id(fleet_id.clone()).await.unwrap().unwrap();

        // then
        assert_eq!(fleet.fleet_id, fleet_id);
        assert_eq!(fleet.fleet_name, fleet_name);
    }

    #[tokio::test]
    async fn should_not_allow_duplicate_fleet_names() {
        // given
        let (_, fleet_service) = setup();

        let fleet_name = "fleet_name".to_string();

        // when
        let _ = fleet_service.create_new_fleet(fleet_name.clone()).await.unwrap();
        let result = fleet_service.create_new_fleet(fleet_name.clone()).await.unwrap_err();

        // then
        assert!(matches!(result, FleetAlreadyExists));
    }
}
