use aws_sdk_sns::error::SdkError;
use aws_sdk_sns::operation::publish::PublishError;
use thiserror::Error;

#[derive(Debug, Error)]
pub enum SNSError {
    #[error("Error while publishing message: {0}")]
    PublishMessage(#[from] SdkError<PublishError>),

    #[error("Invalid message type")]
    InvalidMessageType
}