use std::sync::Arc;

use axum::Router;
use lambda_http::{run, tracing, Error};
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;

use shared::infra::health::health_router;
use users::adapter::inbound::user_handler::user_router;
use users::adapter::outbound::user_repository_impl::UserRepositoryImpl;
use users::domain::service::user_service_impl::UserServiceImpl;
use users::{Config, UserHandlerState};

#[tokio::main]
async fn main() -> Result<(), Error> {
    dotenvy::dotenv().ok();
    tracing::init_default_subscriber();

    let Config { database_config } = Config::default();

    let pool = PgPoolOptions::new()
        .max_connections(database_config.max_connections)
        .connect(&database_config.url)
        .await
        .expect("Failed to create pool");
    migrate!().run(&pool).await.expect("Failed to migrate");

    // Repositories
    let user_repository = Arc::new(UserRepositoryImpl::new(pool));

    // Services
    let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

    let user_handler_state = UserHandlerState {
        user_service: user_service.clone(),
    };

    let health_router = health_router("users");
    let user_router = user_router().with_state(user_handler_state);

    let app = Router::new().merge(health_router).merge(user_router);

    run(app).await
}
