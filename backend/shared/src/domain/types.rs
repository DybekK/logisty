use serde::{Deserialize, Serialize};
use sqlx::Type;

pub mod id;

#[derive(Debug, Clone, Serialize, Deserialize, Type)]
#[sqlx(type_name = "VARCHAR")]
pub enum Role {
    Admin,
    Dispatcher,
    Driver,
}