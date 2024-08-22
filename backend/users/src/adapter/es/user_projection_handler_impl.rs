use crate::domain::error::UserError;
use crate::domain::port::invitation_service::InvitationService;
use crate::domain::port::user_projection_handler::UserProjectionHandler;
use crate::domain::port::user_service::UserService;
use async_trait::async_trait;
use shared::domain::event::users::UserRegisteredPayload;
use tracing::info;

#[derive(Clone)]
pub struct UserProjectionHandlerImpl<UserServiceI, InvitationServiceI>
where
    UserServiceI: UserService,
    InvitationServiceI: InvitationService,
{
    user_service: UserServiceI,
    invitation_service: InvitationServiceI,
}

impl<UserServiceI, InvitationServiceI> UserProjectionHandlerImpl<UserServiceI, InvitationServiceI>
where
    UserServiceI: UserService,
    InvitationServiceI: InvitationService,
{
    pub fn new(user_service: UserServiceI, invitation_service: InvitationServiceI) -> Self {
        UserProjectionHandlerImpl {
            user_service,
            invitation_service,
        }
    }
}

#[async_trait]
impl<UserServiceI, InvitationServiceI> UserProjectionHandler for UserProjectionHandlerImpl<UserServiceI, InvitationServiceI>
where
    UserServiceI: UserService,
    InvitationServiceI: InvitationService,
{
    async fn handle_registered_user(&self, payload: UserRegisteredPayload) -> Result<(), UserError> {
        info!("Making projection of user registered event: {:?}", payload);

        self.invitation_service
            .accept_invitation(payload.invitation_id, payload.accepted_at)
            .await?;
        self.user_service
            .register_user(
                payload.fleet_id,
                payload.first_name,
                payload.last_name,
                payload.email,
                payload.password,
                payload.role,
                payload.accepted_at,
            )
            .await?;

        Ok(())
    }
}
