use crate::domain::port::invitation_service::InvitationService;
use crate::domain::port::user_service::UserService;
use std::env;

pub mod adapter;
pub mod domain;

#[cfg(test)]
pub mod test {
    mod adapter;
    mod domain;
    pub mod fake;
}

pub struct DatabaseConfig {
    pub url: String,
    pub max_connections: u32,
}

pub struct Config {
    pub database_config: DatabaseConfig,
}

impl Default for Config {
    fn default() -> Self {
        Config {
            database_config: DatabaseConfig {
                url: env::var("DATABASE_URL").expect("DATABASE_URL must be set"),
                max_connections: 5,
            },
        }
    }
}

#[derive(Clone)]
pub struct UserHandlerState<UserServiceI>
where
    UserServiceI: UserService,
{
    pub user_service: UserServiceI,
}

#[derive(Clone)]
pub struct InvitationHandlerState<InvitationServiceI>
where
    InvitationServiceI: InvitationService,
{
    pub invitation_service: InvitationServiceI,
}
