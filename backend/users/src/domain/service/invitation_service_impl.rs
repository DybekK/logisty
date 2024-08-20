use crate::domain::error::InvitationError;
use crate::domain::port::invitation_repository::InvitationRepository;
use crate::domain::port::invitation_service::InvitationService;

use async_trait::async_trait;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;

#[derive(Clone)]
pub struct InvitationServiceImpl<InvitationRepositoryI>
where
    InvitationRepositoryI: InvitationRepository,
{
    invitation_repository: InvitationRepositoryI,
}

impl<InvitationRepositoryI> InvitationServiceImpl<InvitationRepositoryI>
where
    InvitationRepositoryI: InvitationRepository,
{
    pub fn new(invitation_repository: InvitationRepositoryI) -> InvitationServiceImpl<InvitationRepositoryI> {
        InvitationServiceImpl { invitation_repository }
    }
}

#[async_trait]
impl<InvitationRepositoryI> InvitationService for InvitationServiceImpl<InvitationRepositoryI>
where
    InvitationRepositoryI: InvitationRepository,
{
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
