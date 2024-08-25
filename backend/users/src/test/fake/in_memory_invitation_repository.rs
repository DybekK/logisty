use std::sync::{Arc, Mutex};

use crate::domain::model::Invitation;
use crate::domain::port::invitation_repository::InvitationRepository;
use async_trait::async_trait;
use chrono::{Duration, NaiveDateTime};
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;

#[derive(Clone)]
pub struct InMemoryInvitationRepository {
    invitations: Arc<Mutex<Vec<Invitation>>>,
}

impl InMemoryInvitationRepository {
    pub fn new() -> Self {
        InMemoryInvitationRepository {
            invitations: Arc::new(Mutex::new(Vec::new())),
        }
    }
}

#[async_trait]
impl InvitationRepository for InMemoryInvitationRepository {
    async fn find_by_id(&self, invitation_id: InvitationId) -> Result<Option<Invitation>, DatabaseError> {
        let invitations = self.invitations.lock().unwrap();
        let invitation = invitations.iter().find(|inv| inv.invitation_id == invitation_id).cloned();

        Ok(invitation)
    }

    async fn find_by_email(&self, email: String) -> Result<Option<Invitation>, DatabaseError> {
        let invitations = self.invitations.lock().unwrap();
        let invitation = invitations.iter().find(|inv| inv.email == email).cloned();

        Ok(invitation)
    }

    async fn accept(&self, invitation_id: InvitationId, accepted_at: NaiveDateTime) -> Result<NaiveDateTime, DatabaseError> {
        let mut invitations = self.invitations.lock().unwrap();
        let invitation = invitations
            .iter_mut()
            .find(|inv| inv.invitation_id == invitation_id)
            .expect("Invitation not found");

        invitation.accepted_at = Some(accepted_at);

        Ok(accepted_at)
    }

    async fn insert(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
        created_at: NaiveDateTime,
    ) -> Result<InvitationId, DatabaseError> {
        let mut invitations = self.invitations.lock().unwrap();

        let invitation_id = InvitationId::default();
        let due_at = created_at + Duration::days(7);

        let invitation = Invitation {
            invitation_id: invitation_id.clone(),
            fleet_id,
            role,
            first_name,
            last_name,
            email,
            due_at,
            created_at,
            accepted_at: None,
        };
        invitations.push(invitation);

        Ok(invitation_id)
    }
}
