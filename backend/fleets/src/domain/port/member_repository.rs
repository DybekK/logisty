use async_trait::async_trait;
use auto_impl::auto_impl;

use shared::domain::types::id::{FleetId, FleetMemberId, UserId};
use shared::infra::database::error::DatabaseError;

#[async_trait]
#[auto_impl(Arc)]
pub trait MemberRepository: Sync + Send {
    async fn insert(
        &self,
        fleet_id: FleetId,
        user_id: UserId,
    ) -> Result<FleetMemberId, DatabaseError>;
}