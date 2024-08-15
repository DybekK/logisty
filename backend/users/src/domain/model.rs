use serde::{Deserialize, Serialize};

use shared::domain::types::id::UserId;

#[derive(Clone, Serialize, Deserialize, sqlx::FromRow)]
pub struct User {
    pub id: UserId,
    pub email: String,
    pub password: String,
}
