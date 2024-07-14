use serde::{Deserialize, Serialize};
use crate::domain::UserId;

#[derive(Clone, Serialize, Deserialize, sqlx::FromRow)]
pub struct User {
    pub id: UserId,
    pub email: String,
    pub password: String,
}