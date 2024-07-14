use std::sync::Arc;

use axum::{Router, routing::post};
use axum::routing::get;
use lambda_http::{Error, run, tracing};

use users::{AppState, Config};
use users::adapter::inbound::user_handler::{get_user_handler, register_user_handler};
use users::adapter::outbound::user_repository_impl::UserRepositoryImpl;
use users::domain::service::user_service_impl::UserServiceImpl;

#[tokio::main]
async fn main() -> Result<(), Error> {
    dotenvy::dotenv().ok();
    tracing::init_default_subscriber();

    let config = Config::default();
    let pool = sqlx::postgres::PgPoolOptions::new()
        .max_connections(config.database_max_connections)
        .connect(&config.database_url)
        .await
        .expect("Failed to create pool");

    let user_repository = Arc::new(UserRepositoryImpl::new(pool));
    let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

    let app_state = AppState {
        user_repository: user_repository.clone(),
        user_service: user_service.clone(),
    };

    let app = Router::new()
        .route("/user/:user_id", get(get_user_handler))
        .route("/user/register", post(register_user_handler))
        .with_state(app_state);

    run(app).await
}