use async_trait::async_trait;
use auto_impl::auto_impl;
use serde::Serialize;

use crate::infra::sns::error::SNSError;

pub trait SNSMessage: Serialize + Clone + Sync + Send + 'static {}

#[async_trait]
#[auto_impl(Arc)]
pub trait SNSClient: Clone + Sync + Send + 'static {
    async fn publish<T: SNSMessage>(&self, message: T) -> Result<Option<String>, SNSError>;
}
