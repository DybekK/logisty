use std::env;
use std::sync::Arc;
use axum::extract::FromRef;
use crate::adapter::outbound::user_repository_impl::UserRepositoryImpl;
use crate::domain::service::user_service_impl::UserServiceImpl;

pub mod adapter;
pub mod domain;

#[cfg(test)]
pub mod test {
    pub mod fake;
}

pub struct Config {
    pub database_url: String,
    pub database_max_connections: u32,
}

impl Default for Config {
    fn default() -> Self {
        Config {
            database_url: env::var("DATABASE_URL").unwrap(),
            database_max_connections: 5,
        }
    }
}

type UserRepositoryArc = Arc<UserRepositoryImpl>;
type UserServiceArc = Arc<UserServiceImpl<UserRepositoryArc>>;

#[derive(Clone, FromRef)]
pub struct AppState {
    pub user_repository: UserRepositoryArc,
    pub user_service: UserServiceArc,
}


