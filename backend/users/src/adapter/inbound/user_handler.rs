extern crate serde_json;

use axum::extract::{Path, State};
use axum::Json;
use axum::response::IntoResponse;
use lambda_http::http::StatusCode;

use shared::domain::types::id::UserId;
use crate::adapter::dto::RegisterNewUser;
use crate::AppState;
use crate::domain::port::user_service::UserService;

pub async fn get_user_handler(
    State(AppState { user_service, .. }): State<AppState>,
    Path(user_id): Path<UserId>,
) -> impl IntoResponse {
    match user_service.get_user(user_id).await {
        Ok(user_id) => (StatusCode::OK, Json(user_id)).into_response(),
        Err(_) => StatusCode::INTERNAL_SERVER_ERROR.into_response(),
    }
}

pub async fn register_user_handler(
    State(AppState { user_service, .. }): State<AppState>,
    Json(payload): Json<RegisterNewUser>,
) -> impl IntoResponse {
    match user_service.register_user(payload.email, payload.password).await {
        Ok(user_id) => (StatusCode::OK, Json(user_id)).into_response(),
        Err(_) => StatusCode::INTERNAL_SERVER_ERROR.into_response(),
    }
}
