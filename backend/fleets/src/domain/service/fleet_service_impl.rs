use async_trait::async_trait;

use shared::domain::types::id::FleetId;

use crate::domain::error::FleetError;
use crate::domain::error::FleetError::FleetAlreadyExists;
use crate::domain::port::fleet_repository::FleetRepository;
use crate::domain::port::fleet_service::FleetService;

#[derive(Clone)]
pub struct FleetServiceImpl<FleetRepositoryI>
where
    FleetRepositoryI: FleetRepository,
{
    repository: FleetRepositoryI,
}

impl<FleetRepositoryI> FleetServiceImpl<FleetRepositoryI>
where
    FleetRepositoryI: FleetRepository,
{
    pub fn new(repository: FleetRepositoryI) -> FleetServiceImpl<FleetRepositoryI> {
        FleetServiceImpl { repository }
    }
}

#[async_trait]
impl<FleetRepositoryI> FleetService for FleetServiceImpl<FleetRepositoryI>
where
    FleetRepositoryI: FleetRepository,
{
    async fn create_new_fleet(&self, fleet_name: String) -> Result<FleetId, FleetError> {
        match self.repository.find_by_name(fleet_name.clone()).await? {
            Some(_) => Err(FleetAlreadyExists.into()),
            None => Ok(self.repository.insert(fleet_name.clone()).await?),
        }
    }
}
