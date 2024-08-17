use async_trait::async_trait;

use shared::domain::port::user_http_client::UserHttpClient;
use shared::domain::types::id::FleetId;
use shared::infra::sns::sns_client::SNSClient;

use crate::domain::error::MemberInvitationError;
use crate::domain::error::MemberInvitationError::{FleetNotExists, MemberAlreadyExists};
use crate::domain::event::user_invited_event::UserInvitedEvent;
use crate::domain::port::fleet_repository::FleetRepository;
use crate::domain::port::member_invitation_dispatcher::MemberInvitationDispatcher;

#[derive(Clone)]
pub struct MemberInvitationDispatcherImpl<SNSClientI, UserHttpClientI, FleetRepositoryI>
where
    SNSClientI: SNSClient,
    UserHttpClientI: UserHttpClient,
    FleetRepositoryI: FleetRepository,
{
    sns_client: SNSClientI,
    user_http_client: UserHttpClientI,
    fleet_repository: FleetRepositoryI,
}

impl<SNSClientI, UserHttpClientI, FleetRepositoryI> MemberInvitationDispatcherImpl<SNSClientI, UserHttpClientI, FleetRepositoryI>
where
    SNSClientI: SNSClient,
    UserHttpClientI: UserHttpClient,
    FleetRepositoryI: FleetRepository,
{
    pub fn new(
        sns_client: SNSClientI,
        user_http_client: UserHttpClientI,
        fleet_repository: FleetRepositoryI,
    ) -> MemberInvitationDispatcherImpl<SNSClientI, UserHttpClientI, FleetRepositoryI> {
        MemberInvitationDispatcherImpl {
            sns_client,
            user_http_client,
            fleet_repository,
        }
    }

    async fn validate_fleet(&self, fleet_id: FleetId) -> Result<(), MemberInvitationError> {
        if self.fleet_repository.find_by_id(fleet_id.clone()).await?.is_none() {
            return Err(FleetNotExists);
        }

        Ok(())
    }

    async fn validate_member(&self, email: String) -> Result<(), MemberInvitationError> {
        if self.user_http_client.get_user_by_email(email.clone()).await?.is_some() {
            return Err(MemberAlreadyExists);
        }

        Ok(())
    }
}

#[async_trait]
impl<SNSClientI, UserHttpClientI, FleetRepositoryI> MemberInvitationDispatcher
    for MemberInvitationDispatcherImpl<SNSClientI, UserHttpClientI, FleetRepositoryI>
where
    SNSClientI: SNSClient,
    UserHttpClientI: UserHttpClient,
    FleetRepositoryI: FleetRepository,
{
    async fn invite_member(
        &self,
        fleet_id: FleetId,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<(), MemberInvitationError> {
        self.validate_fleet(fleet_id.clone()).await?;
        self.validate_member(email.clone()).await?;

        let event = UserInvitedEvent::new(fleet_id.clone(), first_name.clone(), last_name.clone(), email.clone());
        self.sns_client.publish(event).await?;

        Ok(())
    }
}
