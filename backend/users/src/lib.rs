use crate::domain::port::invitation_service::InvitationService;
use crate::domain::port::user_service::UserService;
use domain::port::user_command_handler::UserCommandHandler;
use std::env;

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

#[derive(Clone)]
pub struct SNSTopicArns {
    pub user_registered: String,
}

impl Default for SNSTopicArns {
    fn default() -> Self {
        SNSTopicArns {
            user_registered: "user_registered".to_string(),
        }
    }
}

pub struct Config {
    pub aws_config: AWSConfig,
    pub database_config: DatabaseConfig,
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
            topic_arns: SNSTopicArns {
                user_registered: env::var("USER_REGISTERED_EVENT_TOPIC_ARN")
                    .expect("USER_REGISTERED_EVENT_TOPIC_ARN must be set"),
            },
        }
    }
}

#[derive(Clone)]
pub struct UserHandlerState<UserCommandHandlerI, UserServiceI>
where
    UserCommandHandlerI: UserCommandHandler,
    UserServiceI: UserService,
{
    pub command_handler: UserCommandHandlerI,
    pub user_service: UserServiceI,
}

#[derive(Clone)]
pub struct InvitationHandlerState<InvitationServiceI>
where
    InvitationServiceI: InvitationService,
{
    pub invitation_service: InvitationServiceI,
}
