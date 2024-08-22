use std::sync::{Arc, Mutex};

use async_trait::async_trait;
use serde_json::to_string;
use uuid::Uuid;

use crate::infra::queue::error::SNSError;
use crate::infra::queue::sns_client::{SNSClient, SerializableMessage};

#[derive(Clone)]
pub struct Message {
    pub topic_arn: String,
    pub message_id: String,
    pub message: String,
}

#[derive(Clone)]
pub struct InMemorySNSClient {
    messages: Arc<Mutex<Vec<Message>>>,
}

impl InMemorySNSClient {
    pub fn new() -> Self {
        Self {
            messages: Arc::new(Mutex::new(Vec::new())),
        }
    }

    pub fn get_messages(&self) -> Vec<Message> {
        self.messages.lock().unwrap().clone()
    }
}

#[async_trait]
impl SNSClient for InMemorySNSClient {
    async fn publish<T: SerializableMessage>(&self, topic_arn: &String, message: T) -> Result<Option<String>, SNSError> {
        let message = Message {
            topic_arn: topic_arn.to_string(),
            message_id: Uuid::new_v4().to_string(),
            message: to_string(&message)?,
        };
        self.messages.lock().unwrap().push(message.clone());

        Ok(Some(message.message_id))
    }
}
