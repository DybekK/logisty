use crate::domain::error::InvitationError;

use async_trait::async_trait;
use auto_impl::auto_impl;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;

#[async_trait]
#[auto_impl(Arc)]
pub trait InvitationService: Clone + Sync + Send {
    async fn is_invitation_active(&self, invitation_id: InvitationId) -> Result<bool, InvitationError>;

    async fn create_invitation(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<InvitationId, InvitationError>;
}
