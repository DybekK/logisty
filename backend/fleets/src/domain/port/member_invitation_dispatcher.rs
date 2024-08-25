use async_trait::async_trait;
use auto_impl::auto_impl;

use crate::domain::error::MemberInvitationError;
use shared::domain::types::id::FleetId;
use shared::domain::types::Role;

#[async_trait]
#[auto_impl(Arc)]
pub trait MemberInvitationDispatcher: Clone + Sync + Send {
    async fn invite_member(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<(), MemberInvitationError>;
}
