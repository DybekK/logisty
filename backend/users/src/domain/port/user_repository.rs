use async_trait::async_trait;
use std::error::Error;
use auto_impl::auto_impl;

use shared::domain::types::id::UserId;
use crate::domain::model::User;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserRepository {
    async fn find_by_id(&self, id: UserId) -> Result<Option<User>, Box<dyn Error>>;
    async fn insert(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>>;
}
