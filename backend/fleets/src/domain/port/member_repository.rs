use async_trait::async_trait;
use auto_impl::auto_impl;

use shared::domain::types::id::FleetId;
use shared::infra::database::error::DatabaseError;

#[async_trait]
#[auto_impl(Arc)]
pub trait MemberRepository: Sync + Send + 'static {
    async fn insert(&self, 
        fleet_id: FleetId, 
        first_name: String, 
        last_name: String, 
        email: String
    ) -> Result<FleetId, DatabaseError>;
}