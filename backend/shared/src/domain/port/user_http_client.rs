use async_trait::async_trait;
use auto_impl::auto_impl;
use serde::{Deserialize, Serialize};

use crate::domain::types::id::UserId;
use crate::infra::http::error::HttpClientError;

#[derive(Serialize, Deserialize, Clone)]
pub struct User {
    pub user_id: UserId,
    pub email: String,
}

#[async_trait]
#[auto_impl(Arc)]
pub trait UserHttpClient: Clone + Send + Sync {
    async fn get_user_by_email(&self, email: String) -> Result<Option<User>, HttpClientError>;
}
