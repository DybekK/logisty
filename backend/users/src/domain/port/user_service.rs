use async_trait::async_trait;
use auto_impl::auto_impl;
use shared::domain::types::id::UserId;
use crate::domain::error::UserError;
use crate::domain::model::User;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserService: Clone + Sync + Send {
    async fn get_user_by(&self, user_id: Option<UserId>, email: Option<String>) -> Result<Option<User>, UserError>;
}
