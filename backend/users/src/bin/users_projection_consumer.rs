use aws_lambda_events::sns::SnsMessage;
use aws_lambda_events::sqs::SqsEventObj;
use lambda_runtime::{run, service_fn, tracing, LambdaEvent};
use serde_json::from_str;
use shared::domain::event::Event;
use shared::domain::event::Event::UserRegistered;
use shared::infra::time::SystemTimeProvider;
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;
use std::sync::Arc;
use tracing::info;
use users::adapter::es::user_projection_handler_impl::UserProjectionHandlerImpl;
use users::adapter::outbound::invitation_repository_impl::InvitationRepositoryImpl;
use users::adapter::outbound::user_repository_impl::UserRepositoryImpl;
use users::domain::port::user_projection_handler::UserProjectionHandler;
use users::domain::service::invitation_service_impl::InvitationServiceImpl;
use users::domain::service::user_service_impl::UserServiceImpl;
use users::Config;

#[tokio::main]
async fn main() -> Result<(), lambda_runtime::Error> {
    dotenvy::dotenv().ok();
    tracing::init_default_subscriber();

    let Config { database_config, .. } = Config::default();

    let pool = PgPoolOptions::new()
        .max_connections(database_config.max_connections)
        .connect(&database_config.url)
        .await
        .expect("Failed to create pool");
    migrate!().run(&pool).await.expect("Failed to migrate");

    let system_time_provider = SystemTimeProvider;

    // Repositories
    let user_repository = Arc::new(UserRepositoryImpl::new(pool.clone()));
    let invitation_repository = Arc::new(InvitationRepositoryImpl::new(pool.clone()));

    // Services
    let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));
    let invitation_service = Arc::new(InvitationServiceImpl::new(
        system_time_provider.clone(),
        invitation_repository.clone(),
    ));

    let user_projection_handler = UserProjectionHandlerImpl::new(user_service.clone(), invitation_service.clone());

    run(service_fn(|event| consumer(user_projection_handler.clone(), event))).await
}

pub async fn consumer<UserProjectionHandlerI>(
    user_projection_handler: UserProjectionHandlerI,
    event: LambdaEvent<SqsEventObj<SnsMessage>>,
) -> Result<(), Box<dyn std::error::Error>>
where
    UserProjectionHandlerI: UserProjectionHandler,
{
    for message in event.payload.records {
        let raw_message = message.body.message;

        match from_str::<Event>(&raw_message)? {
            UserRegistered(payload) => user_projection_handler.handle_registered_user(payload).await?,
            _ => info!("Event {} is not supported by users_projection", raw_message),
        }
    }

    Ok(())
}
