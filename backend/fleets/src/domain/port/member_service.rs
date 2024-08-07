use async_trait::async_trait;
use auto_impl::auto_impl;

use shared::domain::types::id::FleetId;

use crate::domain::error::FleetError;

#[async_trait]
#[auto_impl(Arc)]
pub trait MemberService {
    async fn find_by_email(&self, email: String) -> Result<Option<FleetId>, FleetError>;
    async fn add_new_member(
        &self,
        fleet_id: FleetId,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<FleetId, FleetError>;
}
