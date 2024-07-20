use aws_lambda_events::event::sqs::SqsEvent;
use lambda_runtime::{run, service_fn, Error, LambdaEvent};

async fn sqs_consumer_handler(event: LambdaEvent<SqsEvent>) -> Result<(), Error> {
    event
        .payload
        .records
        .iter()
        .for_each(|record| tracing::info!("Message body: {}", record.body.as_deref().unwrap_or_default()));

    Ok(())
}

#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt()
        .with_max_level(tracing::Level::INFO)
        .with_target(false)
        .without_time()
        .init();

    run(service_fn(sqs_consumer_handler)).await
}
