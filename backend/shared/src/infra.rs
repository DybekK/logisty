use std::error::Error;

use axum::http::StatusCode;
use axum::response::{IntoResponse, Response};
use axum::Json;
use serde::{Deserialize, Serialize};
use tracing::error;

pub mod database;
pub mod health;
pub mod http;
pub mod queue;
pub mod time;

#[derive(Deserialize, Serialize)]
pub struct ErrorResponse {
    pub reason: String,
}

pub fn success_response<T: Serialize>(status_code: StatusCode, body: T) -> Response {
    (status_code, Json(body)).into_response()
}

pub fn error_response(status_code: StatusCode, error: Box<dyn Error>) -> Response {
    let response = ErrorResponse {
        reason: error.to_string(),
    };

    (status_code, Json(response)).into_response()
}

pub fn internal_server_error_response(error: Box<dyn Error>) -> Response {
    error!("Internal server error: {:?}", error);
    error_response(StatusCode::INTERNAL_SERVER_ERROR, "Internal server error".into())
}
