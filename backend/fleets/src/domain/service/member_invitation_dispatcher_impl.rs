use crate::domain::error::MemberInvitationError;
use crate::domain::error::MemberInvitationError::{FleetNotExists, MemberAlreadyExists};
use crate::domain::port::fleet_repository::FleetRepository;
use crate::domain::port::member_invitation_dispatcher::MemberInvitationDispatcher;
use crate::SNSTopicArns;
use async_trait::async_trait;
use shared::domain::event::users::user_invited;
use shared::domain::port::user_http_client::UserHttpClient;
use shared::domain::types::id::FleetId;
use shared::domain::types::Role;
use shared::infra::queue::sns_client::SNSClient;
use shared::infra::time::TimeProvider;
use MemberInvitationError::InvitationAlreadyExists;

#[derive(Clone)]
pub struct MemberInvitationDispatcherImpl<TimeProviderI, SNSClientI, UserHttpClientI, FleetRepositoryI>
where
    TimeProviderI: TimeProvider,
    SNSClientI: SNSClient,
    UserHttpClientI: UserHttpClient,
    FleetRepositoryI: FleetRepository,
{
    topics: SNSTopicArns,
    time_provider: TimeProviderI,
    sns_client: SNSClientI,
    user_http_client: UserHttpClientI,
    fleet_repository: FleetRepositoryI,
}

impl<TimeProviderI, SNSClientI, UserHttpClientI, FleetRepositoryI>
    MemberInvitationDispatcherImpl<TimeProviderI, SNSClientI, UserHttpClientI, FleetRepositoryI>
where
    TimeProviderI: TimeProvider,
    SNSClientI: SNSClient,
    UserHttpClientI: UserHttpClient,
    FleetRepositoryI: FleetRepository,
{
    pub fn new(
        topics: SNSTopicArns,
        time_provider: TimeProviderI,
        sns_client: SNSClientI,
        user_http_client: UserHttpClientI,
        fleet_repository: FleetRepositoryI,
    ) -> MemberInvitationDispatcherImpl<TimeProviderI, SNSClientI, UserHttpClientI, FleetRepositoryI> {
        MemberInvitationDispatcherImpl {
            topics,
            time_provider,
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

    //todo: write unit tests
    async fn validate_invitation(&self, email: String) -> Result<(), MemberInvitationError> {
        let invitation = self.user_http_client.get_invitation_by_email(email.clone()).await?;

        if let Some(invitation) = invitation {
            if invitation.accepted_at.is_none() {
                return Err(InvitationAlreadyExists);
            }
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
impl<TimeProviderI, SNSClientI, UserHttpClientI, FleetRepositoryI> MemberInvitationDispatcher
    for MemberInvitationDispatcherImpl<TimeProviderI, SNSClientI, UserHttpClientI, FleetRepositoryI>
where
    TimeProviderI: TimeProvider,
    SNSClientI: SNSClient,
    UserHttpClientI: UserHttpClient,
    FleetRepositoryI: FleetRepository,
{
    async fn invite_member(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<(), MemberInvitationError> {
        self.validate_fleet(fleet_id.clone()).await?;
        self.validate_member(email.clone()).await?;
        self.validate_invitation(email.clone()).await?;

        let event = user_invited(&self.time_provider)(
            fleet_id.clone(),
            role.clone(),
            first_name.clone(),
            last_name.clone(),
            email.clone(),
        );

        self.sns_client.publish(&self.topics.user_invited, event).await?;

        Ok(())
    }
}
