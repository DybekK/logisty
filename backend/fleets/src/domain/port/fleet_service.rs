use async_trait::async_trait;
use auto_impl::auto_impl;

use crate::domain::error::FleetError;
use shared::domain::types::id::FleetId;

#[async_trait]
#[auto_impl(Arc)]
pub trait FleetService: Clone + Sync + Send {
    async fn create_new_fleet(&self, fleet_name: String) -> Result<FleetId, FleetError>;
}
