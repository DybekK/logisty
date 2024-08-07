extern crate serde_json;

use axum::extract::State;
use axum::response::IntoResponse;
use axum::routing::post;
use axum::{Json, Router};
use lambda_http::http::StatusCode;

use shared::infra::error_json;

use crate::adapter::dto::CreateNewFleet;
use crate::domain::error::FleetError::FleetAlreadyExists;
use crate::domain::port::fleet_repository::FleetRepository;
use crate::domain::port::fleet_service::FleetService;
use crate::FleetHandlerState;

pub fn fleet_router<FleetRepositoryI: FleetRepository, FleetServiceI: FleetService>(
) -> Router<FleetHandlerState<FleetRepositoryI, FleetServiceI>> {
    Router::new().route(
        "/create_fleet",
        post(create_new_fleet_handler),
    )
}

async fn create_new_fleet_handler<FleetRepositoryI: FleetRepository, FleetServiceI: FleetService>(
    State(state): State<FleetHandlerState<FleetRepositoryI, FleetServiceI>>,
    Json(payload): Json<CreateNewFleet>,
) -> impl IntoResponse {
    match state.fleet_service.create_new_fleet(payload.fleet_name).await {
        Ok(fleet_id) => (StatusCode::OK, Json(fleet_id)).into_response(),
        Err(FleetAlreadyExists) => (StatusCode::BAD_REQUEST, error_json(FleetAlreadyExists.to_string())).into_response(),
        _ => StatusCode::INTERNAL_SERVER_ERROR.into_response(),
    }
}
