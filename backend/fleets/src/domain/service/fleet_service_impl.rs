use crate::domain::port::fleet_repository::FleetRepository;
use crate::domain::port::fleet_service::FleetService;
use async_trait::async_trait;
use shared::domain::types::id::FleetId;
use std::error::Error;

pub struct FleetServiceImpl<T: FleetRepository> {
    repository: T,
}

impl<T: FleetRepository + Sync + Send> FleetServiceImpl<T> {
    pub fn new(repository: T) -> FleetServiceImpl<T> {
        FleetServiceImpl { repository }
    }
}

#[async_trait]
impl<T: FleetRepository + Sync + Send> FleetService for FleetServiceImpl<T> {
    async fn create_new_fleet(&self, fleet_name: String) -> Result<FleetId, Box<dyn Error>> {
        self.repository.insert(fleet_name).await
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::sync::Arc;

    use crate::test::fake::in_memory_fleet_repository::InMemoryFleetRepository;

    type FleetRepositoryArc = Arc<InMemoryFleetRepository>;
    type FleetServiceArc = Arc<FleetServiceImpl<FleetRepositoryArc>>;

    async fn setup() -> (FleetRepositoryArc, FleetServiceArc) {
        let fleet_repository = Arc::new(InMemoryFleetRepository::new());
        let fleet_service = Arc::new(FleetServiceImpl::new(fleet_repository.clone()));

        (fleet_repository, fleet_service)
    }

    #[tokio::test]
    async fn should_create_new_fleet_successfully() {
        let (fleet_repository, fleet_service) = setup().await;

        let fleet_name = "fleet_name".to_string();

        let fleet_id = fleet_service.create_new_fleet(fleet_name.clone()).await.unwrap();
        let fleet = fleet_repository.find_by_id(fleet_id.clone()).await.unwrap().unwrap();

        assert_eq!(fleet.fleet_id, fleet_id);
        assert_eq!(fleet.fleet_name, fleet_name);
    }
}
