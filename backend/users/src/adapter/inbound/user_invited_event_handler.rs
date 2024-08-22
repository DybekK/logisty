use crate::domain::error::InvitationError;
use crate::domain::port::invitation_service::InvitationService;
use shared::domain::event::users::UserInvitedPayload;
use tracing::info;

#[derive(Clone)]
pub struct UserInvitedEventHandler<InvitationServiceI>
where
    InvitationServiceI: InvitationService,
{
    invitation_service: InvitationServiceI,
}

impl<InvitationServiceI> UserInvitedEventHandler<InvitationServiceI>
where
    InvitationServiceI: InvitationService,
{
    pub fn new(invitation_service: InvitationServiceI) -> Self {
        UserInvitedEventHandler { invitation_service }
    }

    pub async fn handle(&self, payload: UserInvitedPayload) -> Result<(), InvitationError> {
        info!("Received user invited event: {:?}", payload);

        self.invitation_service
            .create_invitation(
                payload.fleet_id,
                payload.role,
                payload.first_name,
                payload.last_name,
                payload.email,
                payload.created_at,
            )
            .await?;

        Ok(())
    }
}
