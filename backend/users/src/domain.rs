use serde::{Deserialize, Serialize};

pub mod model;
pub mod port;
pub mod service;

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct UserId(pub String);

impl Default for UserId {
    fn default() -> Self {
        UserId(cuid::cuid2())
    }
}
