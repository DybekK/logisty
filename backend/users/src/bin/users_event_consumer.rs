use aws_lambda_events::sns::SnsMessage;
use aws_lambda_events::sqs::SqsEventObj;
use lambda_runtime::{run, service_fn, tracing, LambdaEvent};
use serde_json::from_str;
use shared::domain::event::Event;
use shared::domain::event::Event::UserInvited;
use shared::infra::time::SystemTimeProvider;
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;
use std::sync::Arc;
use tracing::info;
use users::adapter::inbound::user_invited_event_handler::UserInvitedEventHandler;
use users::adapter::outbound::invitation_repository_impl::InvitationRepositoryImpl;
use users::domain::port::invitation_service::InvitationService;
use users::domain::service::invitation_service_impl::InvitationServiceImpl;
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
    let invitation_repository = Arc::new(InvitationRepositoryImpl::new(pool));

    // Services
    let invitation_service = Arc::new(InvitationServiceImpl::new(
        system_time_provider.clone(),
        invitation_repository.clone(),
    ));
    let user_invited_event_handler = UserInvitedEventHandler::new(invitation_service.clone());

    run(service_fn(|event| consumer(user_invited_event_handler.clone(), event))).await
}

pub async fn consumer<InvitationServiceI>(
    user_invited_event_handler: UserInvitedEventHandler<InvitationServiceI>,
    event: LambdaEvent<SqsEventObj<SnsMessage>>,
) -> Result<(), Box<dyn std::error::Error>>
where
    InvitationServiceI: InvitationService,
{
    for message in event.payload.records {
        let raw_message = message.body.message;

        match from_str::<Event>(&raw_message)? {
            UserInvited(payload) => user_invited_event_handler.handle(payload).await?,
            _ => info!("Event {} is not supported by users_event_consumer", raw_message),
        }
    }

    Ok(())
}
