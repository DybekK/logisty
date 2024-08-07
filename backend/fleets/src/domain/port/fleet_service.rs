use async_trait::async_trait;
use std::error::Error;
use auto_impl::auto_impl;

use shared::domain::types::id::FleetId;

#[async_trait]
#[auto_impl(Arc)]
pub trait FleetService {
    async fn create_new_fleet(&self, fleet_name: String) -> Result<FleetId, Box<dyn Error>>;
}
