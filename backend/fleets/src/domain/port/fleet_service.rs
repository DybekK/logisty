use async_trait::async_trait;
use auto_impl::auto_impl;

use shared::domain::types::id::FleetId;
use crate::domain::error::FleetError;

#[async_trait]
#[auto_impl(Arc)]
pub trait FleetService: Clone + Sync + Send {
    async fn create_new_fleet(&self, fleet_name: String) -> Result<FleetId, FleetError>;
}
