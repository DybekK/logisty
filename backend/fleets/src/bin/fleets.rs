use axum::Router;
use lambda_http::{Error, run, tracing};
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;

use fleets::Config;
use shared::health::health_router;

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

    let app = Router::new().merge(health_router("users"));

    run(app).await
}
