use std::error::Error;

use async_trait::async_trait;
use auto_impl::auto_impl;

use shared::domain::types::id::FleetId;

use crate::domain::model::Fleet;

#[async_trait]
#[auto_impl(Arc)]
pub trait FleetRepository {
    async fn find_by_id(&self, id: FleetId) -> Result<Option<Fleet>, Box<dyn Error>>;
    async fn insert(&self, fleet_name: String) -> Result<FleetId, Box<dyn Error>>;
}
