use aws_config::Region;
use axum::Router;
use lambda_http::{run, tracing, Error};
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;
use std::sync::Arc;

use shared::infra::health::health_router;
use shared::infra::queue::sns_client_impl::SNSClientImpl;
use shared::infra::time::SystemTimeProvider;
use users::adapter::es::user_command_handler_impl::UserCommandHandlerImpl;
use users::adapter::inbound::invitation_handler::invitation_router;
use users::adapter::inbound::user_handler::user_router;
use users::adapter::outbound::invitation_repository_impl::InvitationRepositoryImpl;
use users::adapter::outbound::user_repository_impl::UserRepositoryImpl;
use users::domain::service::invitation_service_impl::InvitationServiceImpl;
use users::domain::service::user_service_impl::UserServiceImpl;
use users::{Config, InvitationHandlerState, UserHandlerState};

#[tokio::main]
async fn main() -> Result<(), Error> {
    dotenvy::dotenv().ok();
    tracing::init_default_subscriber();

    let Config {
        aws_config,
        database_config,
        topic_arns,
    } = Config::default();

    let aws_sdk_config = aws_config::from_env().region(Region::new(aws_config.region)).load().await;

    let pool = PgPoolOptions::new()
        .max_connections(database_config.max_connections)
        .connect(&database_config.url)
        .await
        .expect("Failed to create pool");
    migrate!().run(&pool).await.expect("Failed to migrate");

    let system_time_provider = SystemTimeProvider;

    // SNS
    let sns_client = aws_sdk_sns::Client::new(&aws_sdk_config);
    let default_sns_client = Arc::new(SNSClientImpl::new(sns_client));

    // Repositories
    let user_repository = Arc::new(UserRepositoryImpl::new(pool.clone()));
    let invitation_repository = Arc::new(InvitationRepositoryImpl::new(pool.clone()));

    // Services
    let invitation_service = Arc::new(InvitationServiceImpl::new(
        system_time_provider.clone(),
        invitation_repository.clone(),
    ));
    let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

    // Command Handlers
    let user_command_handler = UserCommandHandlerImpl::new(
        topic_arns.clone(),
        system_time_provider.clone(),
        default_sns_client.clone(),
        invitation_service.clone(),
    );

    let user_handler_state = UserHandlerState {
        command_handler: user_command_handler.clone(),
        user_service: user_service.clone(),
    };

    let invitation_handler_state = InvitationHandlerState {
        invitation_service: invitation_service.clone(),
    };

    let health_router = health_router("users");
    let user_router = user_router().with_state(user_handler_state);
    let invitation_router = invitation_router().with_state(invitation_handler_state);

    let app = Router::new().merge(health_router).merge(user_router).merge(invitation_router);

    run(app).await
}
