use std::env;
use std::sync::Arc;
use axum::extract::FromRef;
use crate::adapter::outbound::fleet_repository_impl::FleetRepositoryImpl;
use crate::domain::service::fleet_service_impl::FleetServiceImpl;

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

type FleetRepositoryArc = Arc<FleetRepositoryImpl>;
type FleetServiceArc = Arc<FleetServiceImpl<FleetRepositoryArc>>;

#[derive(Clone, FromRef)]
pub struct AppState {
    pub fleet_repository: FleetRepositoryArc,
    pub fleet_service: FleetServiceArc,
}


