use aws_sdk_sns::error::SdkError;
use aws_sdk_sns::operation::publish::PublishError;
use thiserror::Error;

#[derive(Debug, Error)]
pub enum SNSError {
    #[error(transparent)]
    PublishMessage(#[from] SdkError<PublishError>),

    #[error(transparent)]
    InvalidMessageFormat(#[from] serde_json::Error),
}
