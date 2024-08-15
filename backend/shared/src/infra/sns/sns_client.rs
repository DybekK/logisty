use auto_impl::auto_impl;
use serde_json::Value;
use std::any::Any;
use async_trait::async_trait;
use crate::infra::sns::error::SNSError;
use crate::infra::sns::error::SNSError::InvalidMessageType;

pub trait SNSMessage: Any + Clone + Sync + Send + 'static {
    fn as_any(&self) -> &dyn Any;
}

impl SNSMessage for String {
    fn as_any(&self) -> &dyn Any {
        self
    }
}

impl SNSMessage for Value {
    fn as_any(&self) -> &dyn Any {
        self
    }
}

#[async_trait]
#[auto_impl(Arc)]
pub trait SNSClient: Clone + Sync + Send + 'static {
    async fn publish<T: SNSMessage>(&self, message: T) -> Result<Option<String>, SNSError>;
}

pub fn transform_message_type<T: SNSMessage>(message: T) -> Result<String, SNSError> {
    if let Some(s) = message.as_any().downcast_ref::<String>() {
        Ok(s.clone())
    } else if let Some(v) = message.as_any().downcast_ref::<Value>() {
        Ok(v.to_string())
    } else {
        Err(InvalidMessageType)
    }
}
