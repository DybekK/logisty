use crate::domain::error::InvitationError;
use crate::domain::port::invitation_repository::InvitationRepository;
use crate::domain::port::invitation_service::InvitationService;

use crate::domain::error::InvitationError::{InvitationInactive, InvitationNotFound};
use crate::domain::model::Invitation;
use async_trait::async_trait;
use chrono::NaiveDateTime;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;
use shared::infra::time::TimeProvider;
use InvitationError::InvalidInvitationSearchCriteria;

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
    async fn get_invitation_by(&self, email: Option<String>) -> Result<Option<Invitation>, InvitationError> {
        match email {
            Some(email) => Ok(self.invitation_repository.find_by_email(email).await?),
            None => Err(InvalidInvitationSearchCriteria),
        }
    }

    async fn get_active_invitation(&self, invitation_id: InvitationId) -> Result<Invitation, InvitationError> {
        let invitation = self
            .invitation_repository
            .find_by_id(invitation_id)
            .await?
            .ok_or(InvitationNotFound)?;

        match invitation.is_active(&self.time_provider) {
            true => Ok(invitation),
            false => Err(InvitationInactive),
        }
    }

    async fn create_invitation(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
        created_at: NaiveDateTime,
    ) -> Result<InvitationId, InvitationError> {
        let invitation_id = self
            .invitation_repository
            .insert(fleet_id, role, first_name, last_name, email, created_at)
            .await?;

        Ok(invitation_id)
    }

    async fn accept_invitation(
        &self,
        invitation_id: InvitationId,
        accepted_at: NaiveDateTime,
    ) -> Result<NaiveDateTime, InvitationError> {
        let accepted_at = self.invitation_repository.accept(invitation_id, accepted_at).await?;

        Ok(accepted_at)
    }
}
