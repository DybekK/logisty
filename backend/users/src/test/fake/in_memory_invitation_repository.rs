use std::sync::{Arc, Mutex};

use crate::domain::model::{Invitation, InvitationStatus};
use crate::domain::port::invitation_repository::InvitationRepository;
use async_trait::async_trait;
use chrono::Duration;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;
use shared::infra::time::TimeProvider;
use InvitationStatus::Pending;

#[derive(Clone)]
pub struct InMemoryInvitationRepository<TimeProviderI>
where
    TimeProviderI: TimeProvider,
{
    time_provider: TimeProviderI,
    invitations: Arc<Mutex<Vec<Invitation>>>,
}

impl<TimeProviderI> InMemoryInvitationRepository<TimeProviderI>
where
    TimeProviderI: TimeProvider,
{
    pub fn new(time_provider: TimeProviderI) -> Self {
        InMemoryInvitationRepository {
            time_provider,
            invitations: Arc::new(Mutex::new(Vec::new())),
        }
    }
}

#[async_trait]
impl<TimeProviderI> InvitationRepository for InMemoryInvitationRepository<TimeProviderI>
where
    TimeProviderI: TimeProvider,
{
    async fn find_by_id(&self, invitation_id: InvitationId) -> Result<Option<Invitation>, DatabaseError> {
        let invitations = self.invitations.lock().unwrap();
        let invitation = invitations.iter().find(|inv| inv.invitation_id == invitation_id).cloned();

        Ok(invitation)
    }

    async fn insert(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<InvitationId, DatabaseError> {
        let mut invitations = self.invitations.lock().unwrap();

        let invitation_id = InvitationId::default();
        let status = Pending;
        let created_at = self.time_provider.now();
        let due_at = created_at + Duration::days(7);

        let invitation = Invitation {
            invitation_id: invitation_id.clone(),
            fleet_id,
            role,
            first_name,
            last_name,
            email,
            status,
            due_at,
            created_at,
            accepted_at: None,
            denied_at: None,
        };
        invitations.push(invitation);

        Ok(invitation_id)
    }
}
