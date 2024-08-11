extern crate serde_json;

use axum::extract::State;
use axum::response::IntoResponse;
use axum::routing::post;
use axum::{Json, Router};
use lambda_http::http::StatusCode;
use serde::{Deserialize, Serialize};
use serde_json::json;

use shared::domain::types::id::FleetId;
use shared::infra::error_json;

use crate::domain::error::MemberInvitationError::{FleetNotExists, MemberAlreadyExists};
use crate::domain::port::member_invitation_dispatcher::MemberInvitationDispatcher;
use crate::MemberHandlerState;

pub fn member_router<MemberInvitationDispatcherI>() -> Router<MemberHandlerState<MemberInvitationDispatcherI>>
where
    MemberInvitationDispatcherI: MemberInvitationDispatcher + 'static,
{
    Router::new().route("/member/invite", post(invite_member_handler::<MemberInvitationDispatcherI>))
}

#[derive(Deserialize, Serialize)]
pub struct InviteMember {
    fleet_id: FleetId,
    first_name: String,
    last_name: String,
    email: String,
}

async fn invite_member_handler<MemberInvitationDispatcherI>(
    State(state): State<MemberHandlerState<MemberInvitationDispatcherI>>,
    Json(payload): Json<InviteMember>,
) -> impl IntoResponse
where
    MemberInvitationDispatcherI: MemberInvitationDispatcher,
{
    let InviteMember {
        fleet_id,
        first_name,
        last_name,
        email,
    } = payload;

    match state
        .invitation_dispatcher
        .invite_member(fleet_id, first_name, last_name, email)
        .await
    {
        Ok(_) => (StatusCode::OK, Json(json!({"message": "Invitation has been sent"}))).into_response(),
        Err(MemberAlreadyExists) => (StatusCode::BAD_REQUEST, error_json(MemberAlreadyExists.to_string())).into_response(),
        Err(FleetNotExists) => (StatusCode::BAD_REQUEST, error_json(FleetNotExists.to_string())).into_response(),
        _ => StatusCode::INTERNAL_SERVER_ERROR.into_response(),
    }
}
