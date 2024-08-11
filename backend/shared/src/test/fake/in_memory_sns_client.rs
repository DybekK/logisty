use crate::infra::sns::error::SNSError;
use crate::infra::sns::sns_client::{transform_message_type, SNSClient, SNSMessage};

use async_trait::async_trait;
use std::sync::{Arc, Mutex};
use uuid::Uuid;

#[derive(Clone)]
pub struct Message {
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
    async fn publish<T: SNSMessage>(&self, message: T) -> Result<Option<String>, SNSError> {
        let message = Message {
            message_id: Uuid::new_v4().to_string(),
            message: transform_message_type(message)?,
        };
        self.messages.lock().unwrap().push(message.clone());

        Ok(Some(message.message_id))
    }
}
