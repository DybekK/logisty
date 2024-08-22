extern crate serde_json;

use axum::extract::State;
use axum::response::Response;
use axum::routing::post;
use axum::{Json, Router};
use lambda_http::http::StatusCode;
use serde::{Deserialize, Serialize};
use serde_json::json;

use shared::infra::{error_response, internal_server_error_response, success_response};

use crate::domain::error::FleetError::FleetAlreadyExists;
use crate::domain::port::fleet_service::FleetService;
use crate::FleetHandlerState;

pub fn fleet_router<FleetServiceI>() -> Router<FleetHandlerState<FleetServiceI>>
where
    FleetServiceI: FleetService + 'static,
{
    Router::new().route("/fleets/create", post(create_new_fleet_handler::<FleetServiceI>))
}

#[derive(Deserialize, Serialize)]
pub struct CreateNewFleetRequest {
    pub fleet_name: String,
}

async fn create_new_fleet_handler<FleetServiceI>(
    State(state): State<FleetHandlerState<FleetServiceI>>,
    Json(payload): Json<CreateNewFleetRequest>,
) -> Response
where
    FleetServiceI: FleetService,
{
    match state.fleet_service.create_new_fleet(payload.fleet_name).await {
        Ok(fleet_id) => success_response(StatusCode::OK, json!({"fleet_id": fleet_id})),
        Err(FleetAlreadyExists) => error_response(StatusCode::BAD_REQUEST, FleetAlreadyExists.into()),

        Err(unknown_error) => internal_server_error_response(unknown_error.into()),
    }
}
