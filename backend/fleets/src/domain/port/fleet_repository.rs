use async_trait::async_trait;
use auto_impl::auto_impl;

use shared::domain::types::id::FleetId;
use shared::infra::database::error::DatabaseError;

use crate::domain::model::Fleet;

#[async_trait]
#[auto_impl(Arc)]
pub trait FleetRepository: Clone + Sync + Send {
    async fn find_by_id(&self, id: FleetId) -> Result<Option<Fleet>, DatabaseError>;
    async fn find_by_name(&self, fleet_name: String) -> Result<Option<Fleet>, DatabaseError>;
    async fn insert(&self, fleet_name: String) -> Result<FleetId, DatabaseError>;
}
