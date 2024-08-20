use std::sync::Arc;

use aws_sdk_sns::config::Region;
use axum::Router;
use lambda_http::{run, tracing, Error};
use sqlx::migrate;
use sqlx::postgres::PgPoolOptions;

use fleets::adapter::inbound::fleet_handler::fleet_router;
use fleets::adapter::inbound::member_handler::member_router;
use fleets::adapter::outbound::fleet_repository_impl::FleetRepositoryImpl;
use fleets::adapter::outbound::user_http_client_impl::UserHttpClientImpl;
use fleets::domain::service::fleet_service_impl::FleetServiceImpl;
use fleets::domain::service::member_invitation_dispatcher_impl::MemberInvitationDispatcherImpl;
use fleets::{Config, FleetHandlerState, MemberHandlerState};
use shared::infra::health::health_router;
use shared::infra::queue::sns_client_impl::SNSClientImpl;

#[tokio::main]
async fn main() -> Result<(), Error> {
    dotenvy::dotenv().ok();
    tracing::init_default_subscriber();

    let Config {
        aws_config,
        database_config,
        http_client_config,
        topic_arns,
    } = Config::default();

    let aws_sdk_config = aws_config::from_env().region(Region::new(aws_config.region)).load().await;

    let pool = PgPoolOptions::new()
        .max_connections(database_config.max_connections)
        .connect(&database_config.url)
        .await
        .expect("Failed to create pool");
    migrate!().run(&pool).await.expect("Failed to migrate");

    // SNS
    let sns_client = aws_sdk_sns::Client::new(&aws_sdk_config);
    let member_invitation_sns_client = Arc::new(SNSClientImpl::new(sns_client, topic_arns.user_invited));

    // Http
    let user_http_client = Arc::new(UserHttpClientImpl::new(http_client_config.users_url));

    // Repositories
    let fleet_repository = Arc::new(FleetRepositoryImpl::new(pool));

    // Services
    let fleet_service = Arc::new(FleetServiceImpl::new(fleet_repository.clone()));
    let invitation_dispatcher = Arc::new(MemberInvitationDispatcherImpl::new(
        member_invitation_sns_client.clone(),
        user_http_client.clone(),
        fleet_repository.clone(),
    ));

    let fleet_handler_state = FleetHandlerState {
        fleet_service: fleet_service.clone(),
    };

    let member_handler_state = MemberHandlerState {
        invitation_dispatcher: invitation_dispatcher.clone(),
    };

    let health_router = health_router("fleets");
    let fleet_router = fleet_router().with_state(fleet_handler_state);
    let member_router = member_router().with_state(member_handler_state);

    let app = Router::new().merge(health_router).merge(fleet_router).merge(member_router);

    run(app).await
}
