use std::env;
use std::sync::Arc;

use crate::domain::port::fleet_service::FleetService;
use crate::domain::port::member_invitation_dispatcher::MemberInvitationDispatcher;

pub mod adapter;
pub mod domain;

#[cfg(test)]
pub mod test {
    mod adapter;
    mod domain;
    pub mod fake;
}

pub struct AWSConfig {
    pub region: String,
}

pub struct DatabaseConfig {
    pub url: String,
    pub max_connections: u32,
}

pub struct HttpClientConfig {
    pub users_url: String,
}

pub struct SNSTopicArns {
    pub user_invited: String,
}

pub struct Config {
    pub aws_config: AWSConfig,
    pub database_config: DatabaseConfig,
    pub http_client_config: HttpClientConfig,
    pub topic_arns: SNSTopicArns,
}

impl Default for Config {
    fn default() -> Self {
        Config {
            aws_config: AWSConfig {
                region: env::var("APP_AWS_REGION").expect("APP_AWS_REGION must be set"),
            },
            database_config: DatabaseConfig {
                url: env::var("DATABASE_URL").expect("DATABASE_URL must be set"),
                max_connections: 5,
            },
            http_client_config: HttpClientConfig {
                users_url: env::var("USERS_URL").expect("USERS_URL must be set")
            },
            topic_arns: SNSTopicArns {
                user_invited: env::var("TOPIC_ARN_USER_INVITED").expect("TOPIC_ARN_USER_INVITED must be set"),
            },
        }
    }
}

#[derive(Clone)]
pub struct FleetHandlerState<FleetServiceI>
where
    FleetServiceI: FleetService,
{
    pub fleet_service: Arc<FleetServiceI>,
}

#[derive(Clone)]
pub struct MemberHandlerState<MemberInvitationDispatcherI>
where
    MemberInvitationDispatcherI: MemberInvitationDispatcher,
{
    pub invitation_dispatcher: Arc<MemberInvitationDispatcherI>,
}
