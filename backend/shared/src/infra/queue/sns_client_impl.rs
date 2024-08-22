use async_trait::async_trait;
use aws_sdk_sns::Client;
use serde_json::to_string;

use crate::infra::queue::error::SNSError;
use crate::infra::queue::sns_client::{SNSClient, SerializableMessage};

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
    async fn publish<T: SerializableMessage>(&self, topic_arn: &String, message: T) -> Result<Option<String>, SNSError> {
        let message_str = to_string(&message)?;

        let request = self.client.publish().topic_arn(topic_arn).message(message_str);
        let response = request.send().await?;

        Ok(response.message_id)
    }
}
