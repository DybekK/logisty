use async_trait::async_trait;
use std::error::Error;
use auto_impl::auto_impl;

use crate::domain::model::User;
use crate::domain::UserId;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserService {
    async fn get_user(&self, id: UserId) -> Result<Option<User>, Box<dyn Error>>;
    async fn register_user(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>>;
}
