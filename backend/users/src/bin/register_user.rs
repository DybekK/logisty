use dotenvy::dotenv;
use lambda_http::{run, service_fn, tracing, Error};
use sqlx::postgres::PgPoolOptions;
use users::adapter::handler::register_user_handler;
use users::application::repository::UserRepositoryImpl;
use users::domain::port::UserServiceImpl;
use users::Config;

#[tokio::main]
async fn main() -> Result<(), Error> {
    dotenv()?;
    tracing::init_default_subscriber();

    let config = Config::default();
    let pool = PgPoolOptions::new()
        .max_connections(config.database_max_connections)
        .connect(&*config.database_url)
        .await?;

    let user_repository = UserRepositoryImpl::new(&pool);
    let user_service = UserServiceImpl::new(&user_repository);

    run(service_fn(|request| register_user_handler(&user_service, request))).await
}
