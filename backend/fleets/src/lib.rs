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

pub struct AWSCredentialsConfig {
    pub access_key_id: String,
    pub secret_access_key: String,
}

pub struct DatabaseConfig {
    pub url: String,
    pub max_connections: u32,
}

pub struct HttpClientConfig {
    pub users_url: String,
}

pub struct Config {
    pub aws_config: AWSConfig,
    pub aws_credentials_config: AWSCredentialsConfig,
    pub database_config: DatabaseConfig,
    pub http_client_config: HttpClientConfig,
}

impl Default for Config {
    fn default() -> Self {
        Config {
            aws_config: AWSConfig {
                region: env::var("AWS_REGION").unwrap(),
            },
            aws_credentials_config: AWSCredentialsConfig {
                access_key_id: env::var("AWS_ACCESS_KEY_ID").unwrap(),
                secret_access_key: env::var("AWS_SECRET_ACCESS_KEY").unwrap(),
            },
            database_config: DatabaseConfig {
                url: env::var("DATABASE_URL").unwrap(),
                max_connections: 5,
            },
            http_client_config: HttpClientConfig {
                users_url: env::var("USERS_URL").unwrap(),
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
