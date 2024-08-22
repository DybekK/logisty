use async_trait::async_trait;
use auto_impl::auto_impl;
use chrono::NaiveDateTime;
use shared::domain::types::id::{FleetId, UserId};
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;

use crate::domain::model::User;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserRepository: Clone + Sync + Send {
    async fn find_by_id(&self, user_id: UserId) -> Result<Option<User>, DatabaseError>;
    async fn find_by_email(&self, email: String) -> Result<Option<User>, DatabaseError>;
    async fn insert(
        &self,
        fleet_id: FleetId,
        first_name: String,
        last_name: String,
        email: String,
        password: String,
        role: Role,
        created_at: NaiveDateTime,
    ) -> Result<UserId, DatabaseError>;
}
