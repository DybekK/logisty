use std::fmt::Debug;
use std::sync::Arc;
use std::time::Duration;

use axum::http::{Request, Response};
use axum::Router;
use lambda_http::{Error, run, tracing};
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;
use tower_http::trace::{OnRequest, OnResponse, TraceLayer};
use tracing::info;

use shared::infra::health::health_router;
use users::{Config, UserHandlerState};
use users::adapter::inbound::user_handler::user_router;
use users::adapter::outbound::user_repository_impl::UserRepositoryImpl;
use users::domain::service::user_service_impl::UserServiceImpl;

#[derive(Clone)]
struct LogOnRequest;

impl<B: Debug> OnRequest<B> for LogOnRequest {
    fn on_request(&mut self, request: &Request<B>, _span: &tracing::Span) {
        info!("Received request: {:?}", request);
    }
}

#[derive(Clone)]
struct LogOnResponse;

impl<B: Debug> OnResponse<B> for LogOnResponse {
    fn on_response(self, response: &Response<B>, latency: Duration, _span: &tracing::Span) {
        info!("Responded with: {:?}", response);
        info!("Request took: {:?}", latency);
    }
}

#[tokio::main]
async fn main() -> Result<(), Error> {
    dotenvy::dotenv().ok();
    tracing::init_default_subscriber();

    let tracing_layer = TraceLayer::new_for_http().on_request(LogOnRequest).on_response(LogOnResponse);

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

    let app = Router::new().merge(health_router).merge(user_router).layer(tracing_layer);

    run(app).await
}
