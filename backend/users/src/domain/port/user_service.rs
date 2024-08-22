use crate::domain::error::UserError;
use crate::domain::model::User;
use async_trait::async_trait;
use auto_impl::auto_impl;
use chrono::NaiveDateTime;
use shared::domain::types::id::{FleetId, UserId};
use shared::domain::types::Role;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserService: Clone + Sync + Send {
    async fn get_user_by(&self, user_id: Option<UserId>, email: Option<String>) -> Result<Option<User>, UserError>;

    async fn register_user(
        &self,
        fleet_id: FleetId,
        first_name: String,
        last_name: String,
        email: String,
        password: String,
        role: Role,
        accepted_at: NaiveDateTime,
    ) -> Result<UserId, UserError>;
}
