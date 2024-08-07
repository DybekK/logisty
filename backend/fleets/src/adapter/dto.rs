use serde::{Deserialize, Serialize};

#[derive(Deserialize, Serialize)]
pub struct CreateNewFleet {
    pub email: String,
    pub password: String,
}
