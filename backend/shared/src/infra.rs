pub mod database;
pub mod health;
pub mod sns;
pub mod http;

use axum::Json;
use serde::{Deserialize, Serialize};

#[derive(Deserialize, Serialize)]
pub struct ErrorResponse {
    pub reason: String,
}

pub fn error_json(reason: String) -> Json<ErrorResponse> {
    Json(ErrorResponse { reason })
}
