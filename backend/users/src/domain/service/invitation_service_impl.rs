use crate::domain::error::InvitationError;
use crate::domain::port::invitation_repository::InvitationRepository;
use crate::domain::port::invitation_service::InvitationService;

use crate::domain::error::InvitationError::InvitationNotFound;
use async_trait::async_trait;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;
use shared::infra::time::TimeProvider;

#[derive(Clone)]
pub struct InvitationServiceImpl<TimeProviderI, InvitationRepositoryI>
where
    TimeProviderI: TimeProvider,
    InvitationRepositoryI: InvitationRepository,
{
    time_provider: TimeProviderI,
    invitation_repository: InvitationRepositoryI,
}

impl<TimeProviderI, InvitationRepositoryI> InvitationServiceImpl<TimeProviderI, InvitationRepositoryI>
where
    TimeProviderI: TimeProvider,
    InvitationRepositoryI: InvitationRepository,
{
    pub fn new(
        time_provider: TimeProviderI,
        invitation_repository: InvitationRepositoryI,
    ) -> InvitationServiceImpl<TimeProviderI, InvitationRepositoryI> {
        InvitationServiceImpl {
            time_provider,
            invitation_repository,
        }
    }
}

#[async_trait]
impl<TimeProviderI, InvitationRepositoryI> InvitationService for InvitationServiceImpl<TimeProviderI, InvitationRepositoryI>
where
    TimeProviderI: TimeProvider,
    InvitationRepositoryI: InvitationRepository,
{
    async fn is_invitation_active(&self, invitation_id: InvitationId) -> Result<bool, InvitationError> {
        let invitation = self
            .invitation_repository
            .find_by_id(invitation_id)
            .await?
            .ok_or(InvitationNotFound)?;

        Ok(invitation.is_active(&self.time_provider))
    }

    async fn create_invitation(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<InvitationId, InvitationError> {
        let invitation_id = self
            .invitation_repository
            .insert(fleet_id, role, first_name, last_name, email)
            .await?;

        Ok(invitation_id)
    }
}
