use std::env;
use std::sync::Arc;
use crate::domain::port::fleet_repository::FleetRepository;
use crate::domain::port::fleet_service::FleetService;

pub mod adapter;
pub mod domain;

#[cfg(test)]
pub mod test {
    mod adapter;
    mod domain;
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

#[derive(Clone)]
pub struct FleetHandlerState<
    FleetRepositoryI: FleetRepository,
    FleetServiceI: FleetService,
> {
    pub fleet_repository: Arc<FleetRepositoryI>,
    pub fleet_service: Arc<FleetServiceI>,
}
