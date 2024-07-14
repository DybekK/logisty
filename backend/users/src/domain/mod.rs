use serde::{Deserialize, Serialize};

pub mod port;
pub mod model;

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct UserId(pub String);

impl UserId {
    pub fn new() -> Self {
        UserId(cuid::cuid2())
    }
}
