use serde::{Deserialize, Serialize};

#[derive(Deserialize, Serialize)]
pub struct RegisterNewUser {
    pub email: String,
    pub password: String,
}
