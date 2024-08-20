extern crate serde_json;

use axum::extract::State;
use axum::response::Response;
use axum::routing::post;
use axum::{Json, Router};
use lambda_http::http::StatusCode;
use serde::{Deserialize, Serialize};
use serde_json::json;

use shared::domain::types::id::FleetId;
use shared::domain::types::Role;
use shared::infra::{error_response, internal_server_error_response, success_response};

use crate::domain::error::MemberInvitationError::{FleetNotExists, MemberAlreadyExists};
use crate::domain::port::member_invitation_dispatcher::MemberInvitationDispatcher;
use crate::MemberHandlerState;

pub fn member_router<MemberInvitationDispatcherI>() -> Router<MemberHandlerState<MemberInvitationDispatcherI>>
where
    MemberInvitationDispatcherI: MemberInvitationDispatcher + 'static,
{
    Router::new().route("/members/invite", post(invite_member_handler::<MemberInvitationDispatcherI>))
}

#[derive(Deserialize, Serialize)]
pub struct InviteMemberRequest {
    fleet_id: FleetId,
    role: Role,
    first_name: String,
    last_name: String,
    email: String,
}

async fn invite_member_handler<MemberInvitationDispatcherI>(
    State(state): State<MemberHandlerState<MemberInvitationDispatcherI>>,
    Json(payload): Json<InviteMemberRequest>,
) -> Response
where
    MemberInvitationDispatcherI: MemberInvitationDispatcher,
{
    let InviteMemberRequest {
        fleet_id,
        role,
        first_name,
        last_name,
        email,
    } = payload;

    match state
        .invitation_dispatcher
        .invite_member(fleet_id, role, first_name, last_name, email)
        .await
    {
        Ok(_) => success_response(StatusCode::OK, json!({"message": "Invitation has been sent"})),
        Err(MemberAlreadyExists) => error_response(StatusCode::BAD_REQUEST, MemberAlreadyExists.into()),
        Err(FleetNotExists) => error_response(StatusCode::BAD_REQUEST, FleetNotExists.into()),

        Err(unknown_error) => internal_server_error_response(unknown_error.into()),
    }
}
