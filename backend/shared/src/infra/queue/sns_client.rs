use async_trait::async_trait;
use auto_impl::auto_impl;
use serde::Serialize;
use std::fmt::Debug;

use crate::infra::queue::error::SNSError;

pub trait SNSMessage: Serialize + Clone + Debug + Sync + Send + 'static {}

#[async_trait]
#[auto_impl(Arc)]
pub trait SNSClient: Clone + Sync + Send + 'static {
    async fn publish<T: SNSMessage>(&self, message: T) -> Result<Option<String>, SNSError>;
}
