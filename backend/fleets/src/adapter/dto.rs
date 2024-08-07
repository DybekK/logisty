use serde::{Deserialize, Serialize};

#[derive(Deserialize, Serialize)]
pub struct CreateNewFleet {
    pub fleet_name: String,
}
