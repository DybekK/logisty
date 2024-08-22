use crate::domain::error::UserError;
use crate::domain::port::invitation_service::InvitationService;
use crate::domain::port::user_command_handler::UserCommandHandler;
use crate::SNSTopicArns;
use async_trait::async_trait;
use shared::domain::event::users::user_registered;
use shared::domain::types::id::InvitationId;
use shared::infra::queue::sns_client::SNSClient;
use shared::infra::time::TimeProvider;

#[derive(Clone)]
pub struct UserCommandHandlerImpl<TimeProviderI, SNSClientI, InvitationServiceI>
where
    TimeProviderI: TimeProvider,
    SNSClientI: SNSClient,
    InvitationServiceI: InvitationService,
{
    topics: SNSTopicArns,
    time_provider: TimeProviderI,
    sns_client: SNSClientI,
    invitation_service: InvitationServiceI,
}

impl<TimeProviderI, SNSClientI, InvitationServiceI> UserCommandHandlerImpl<TimeProviderI, SNSClientI, InvitationServiceI>
where
    TimeProviderI: TimeProvider,
    SNSClientI: SNSClient,
    InvitationServiceI: InvitationService,
{
    pub fn new(
        topics: SNSTopicArns,
        time_provider: TimeProviderI,
        sns_client: SNSClientI,
        invitation_service: InvitationServiceI,
    ) -> UserCommandHandlerImpl<TimeProviderI, SNSClientI, InvitationServiceI> {
        UserCommandHandlerImpl {
            topics,
            time_provider,
            sns_client,
            invitation_service,
        }
    }
}

#[async_trait]
impl<TimeProviderI, SNSClientI, InvitationServiceI> UserCommandHandler
    for UserCommandHandlerImpl<TimeProviderI, SNSClientI, InvitationServiceI>
where
    TimeProviderI: TimeProvider,
    SNSClientI: SNSClient,
    InvitationServiceI: InvitationService,
{
    async fn handle_register_user(&self, invitation_id: InvitationId, password: String) -> Result<(), UserError> {
        let invitation = self.invitation_service.get_active_invitation(invitation_id.clone()).await?;

        let event = user_registered(&self.time_provider)(
            invitation.invitation_id,
            invitation.fleet_id,
            invitation.role,
            invitation.first_name,
            invitation.last_name,
            password,
            invitation.email,
        );

        self.sns_client.publish(&self.topics.user_registered, event).await?;

        Ok(())
    }
}
