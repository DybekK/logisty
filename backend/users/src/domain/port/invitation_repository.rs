use async_trait::async_trait;
use auto_impl::auto_impl;
use chrono::NaiveDateTime;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;

use crate::domain::model::Invitation;

#[async_trait]
#[auto_impl(Arc)]
pub trait InvitationRepository: Clone + Sync + Send {
    async fn find_by_id(&self, invitation_id: InvitationId) -> Result<Option<Invitation>, DatabaseError>;
    async fn find_by_email(&self, email: String) -> Result<Option<Invitation>, DatabaseError>;
    async fn accept(&self, invitation_id: InvitationId, accepted_at: NaiveDateTime) -> Result<NaiveDateTime, DatabaseError>;

    async fn insert(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
        created_at: NaiveDateTime,
    ) -> Result<InvitationId, DatabaseError>;
}
