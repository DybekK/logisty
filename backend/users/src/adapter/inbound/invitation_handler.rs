extern crate serde_json;

use axum::extract::{Path, Query, State};
use axum::response::Response;
use axum::routing::get;
use axum::Router;
use lambda_http::http::StatusCode;
use serde::{Deserialize, Serialize};
use shared::domain::types::id::InvitationId;
use shared::infra::{error_response, success_response};
use InvitationError::{InvalidInvitationSearchCriteria, InvitationInactive, InvitationNotFound};

use crate::domain::error::InvitationError;
use crate::domain::error::UserError::InvalidUserSearchCriteria;
use crate::domain::port::invitation_service::InvitationService;
use crate::InvitationHandlerState;

pub fn invitation_router<InvitationServiceI>() -> Router<InvitationHandlerState<InvitationServiceI>>
where
    InvitationServiceI: InvitationService + 'static,
{
    Router::new()
        .route("/invitations", get(get_invitation_by_handler))
        .route("/invitations/:id/check", get(get_invitation_check_handler))
}

#[derive(Deserialize)]
struct GetInvitationQuery {
    email: Option<String>,
}

//todo: write adapter tests
async fn get_invitation_by_handler<InvitationServiceI>(
    State(state): State<InvitationHandlerState<InvitationServiceI>>,
    Query(params): Query<GetInvitationQuery>,
) -> Response
where
    InvitationServiceI: InvitationService,
{
    match state.invitation_service.get_invitation_by(params.email).await {
        Ok(invitation) => success_response(StatusCode::OK, invitation),
        Err(InvalidInvitationSearchCriteria) => error_response(StatusCode::BAD_REQUEST, InvalidUserSearchCriteria.into()),

        Err(unknown_error) => error_response(StatusCode::INTERNAL_SERVER_ERROR, unknown_error.into()),
    }
}

#[derive(Serialize)]
struct InvitationCheck {
    email: Option<String>,
    is_active: bool,
}

//todo: write adapter tests
async fn get_invitation_check_handler<InvitationServiceI>(
    State(state): State<InvitationHandlerState<InvitationServiceI>>,
    Path(invitation_id): Path<InvitationId>,
) -> Response
where
    InvitationServiceI: InvitationService,
{
    match state.invitation_service.get_active_invitation(invitation_id).await {
        Ok(invitation) => success_response(
            StatusCode::OK,
            InvitationCheck {
                email: Some(invitation.email),
                is_active: true,
            },
        ),
        Err(InvitationInactive) => success_response(
            StatusCode::OK,
            InvitationCheck {
                email: None,
                is_active: false,
            },
        ),
        Err(InvitationNotFound) => error_response(StatusCode::NOT_FOUND, InvitationNotFound.into()),

        Err(unknown_error) => error_response(StatusCode::INTERNAL_SERVER_ERROR, unknown_error.into()),
    }
}
