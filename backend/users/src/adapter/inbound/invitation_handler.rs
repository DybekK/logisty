extern crate serde_json;

use axum::extract::{Path, State};
use axum::response::Response;
use axum::routing::get;
use axum::Router;
use lambda_http::http::StatusCode;
use serde_json::json;
use shared::domain::types::id::InvitationId;
use shared::infra::{error_response, success_response};
use InvitationError::InvitationNotFound;

use crate::domain::error::InvitationError;
use crate::domain::port::invitation_service::InvitationService;
use crate::InvitationHandlerState;

pub fn invitation_router<InvitationServiceI>() -> Router<InvitationHandlerState<InvitationServiceI>>
where
    InvitationServiceI: InvitationService + 'static,
{
    Router::new().route("/invitation/check/:id", get(invitation_check_handler))
}

async fn invitation_check_handler<InvitationServiceI>(
    State(state): State<InvitationHandlerState<InvitationServiceI>>,
    Path(invitation_id): Path<InvitationId>,
) -> Response
where
    InvitationServiceI: InvitationService,
{
    match state.invitation_service.is_invitation_active(invitation_id).await {
        Ok(is_active) => success_response(StatusCode::OK, json!({ "is_active": is_active })),
        Err(InvitationNotFound) => error_response(StatusCode::NOT_FOUND, InvitationNotFound.into()),

        Err(unknown_error) => error_response(StatusCode::INTERNAL_SERVER_ERROR, unknown_error.into()),
    }
}
