use std::env;

pub mod adapter;
pub mod application;
pub mod domain;

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
