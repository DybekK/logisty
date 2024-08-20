use async_trait::async_trait;
use auto_impl::auto_impl;

use shared::domain::types::id::UserId;
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;

use crate::domain::model::User;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserRepository: Clone + Sync + Send {
    async fn find_by_id(&self, user_id: UserId) -> Result<Option<User>, DatabaseError>;
    async fn find_by_email(&self, email: String) -> Result<Option<User>, DatabaseError>;
    async fn insert(&self, email: String, password: String, role: Role) -> Result<UserId, DatabaseError>;
}
