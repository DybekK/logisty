extern crate serde_json;

use axum::extract::State;
use axum::response::IntoResponse;
use axum::routing::post;
use axum::{Json, Router};
use lambda_http::http::StatusCode;
use serde::{Deserialize, Serialize};

use shared::infra::error_json;

use crate::domain::error::FleetError::FleetAlreadyExists;
use crate::domain::port::fleet_service::FleetService;
use crate::FleetHandlerState;

pub fn fleet_router<FleetServiceI>() -> Router<FleetHandlerState<FleetServiceI>>
where
    FleetServiceI: FleetService + 'static,
{
    Router::new().route("/fleet/create", post(create_new_fleet_handler::<FleetServiceI>))
}

#[derive(Deserialize, Serialize)]
pub struct CreateNewFleet {
    pub fleet_name: String,
}

async fn create_new_fleet_handler<FleetServiceI>(
    State(state): State<FleetHandlerState<FleetServiceI>>,
    Json(payload): Json<CreateNewFleet>,
) -> impl IntoResponse
where
    FleetServiceI: FleetService,
{
    match state.fleet_service.create_new_fleet(payload.fleet_name).await {
        Ok(fleet_id) => (StatusCode::OK, Json(fleet_id)).into_response(),
        Err(FleetAlreadyExists) => (StatusCode::BAD_REQUEST, error_json(FleetAlreadyExists.to_string())).into_response(),
        _ => StatusCode::INTERNAL_SERVER_ERROR.into_response(),
    }
}
