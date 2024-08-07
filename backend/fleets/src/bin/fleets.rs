use std::sync::Arc;

use axum::Router;
use lambda_http::{Error, run, tracing};
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;

use fleets::{Config, FleetHandlerState};
use fleets::adapter::inbound::fleet_handler::fleet_router;
use fleets::adapter::outbound::fleet_repository_impl::FleetRepositoryImpl;
use fleets::domain::service::fleet_service_impl::FleetServiceImpl;
use shared::infra::health::health_router;

#[tokio::main]
async fn main() -> Result<(), Error> {
    dotenvy::dotenv().ok();
    tracing::init_default_subscriber();

    let config = Config::default();
    let pool = PgPoolOptions::new()
        .max_connections(config.database_max_connections)
        .connect(&config.database_url)
        .await
        .expect("Failed to create pool");
    migrate!().run(&pool).await.expect("Failed to migrate");

    let fleet_repository = Arc::new(FleetRepositoryImpl::new(pool));
    let fleet_service = Arc::new(FleetServiceImpl::new(fleet_repository.clone()));

    let fleet_handler_state = FleetHandlerState {
        fleet_repository: fleet_repository.clone(),
        fleet_service: fleet_service.clone(),
    };

    let health_router = health_router("fleets");
    let fleet_router = fleet_router().with_state(fleet_handler_state);

    let app = Router::new()
        .merge(health_router)
        .merge(fleet_router);

    run(app).await
}
