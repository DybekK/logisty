use async_trait::async_trait;
use aws_sdk_sns::Client;

use crate::infra::sns::error::SNSError;
use crate::infra::sns::sns_client::{SNSClient, SNSMessage, transform_message_type};

#[derive(Clone)]
pub struct SNSClientImpl {
    client: Client,
}

impl SNSClientImpl {
    pub fn new(client: Client) -> Self {
        Self { client }
    }
}

#[async_trait]
impl SNSClient for SNSClientImpl {
    async fn publish<T: SNSMessage>(&self, message: T) -> Result<Option<String>, SNSError> {
        let message_str = transform_message_type(message)?;

        let request = self.client.publish().message(message_str);
        let response = request.send().await?;

        Ok(response.message_id)
    }
}
