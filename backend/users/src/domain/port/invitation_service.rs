use crate::domain::error::InvitationError;

use crate::domain::model::Invitation;
use async_trait::async_trait;
use auto_impl::auto_impl;
use chrono::NaiveDateTime;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;

#[async_trait]
#[auto_impl(Arc)]
pub trait InvitationService: Clone + Sync + Send {
    async fn get_invitation_by(&self, email: Option<String>) -> Result<Option<Invitation>, InvitationError>;

    async fn get_active_invitation(&self, invitation_id: InvitationId) -> Result<Invitation, InvitationError>;

    async fn create_invitation(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
        created_at: NaiveDateTime,
    ) -> Result<InvitationId, InvitationError>;

    async fn accept_invitation(
        &self,
        invitation_id: InvitationId,
        accepted_at: NaiveDateTime,
    ) -> Result<NaiveDateTime, InvitationError>;
}
