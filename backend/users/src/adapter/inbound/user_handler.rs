extern crate serde_json;

use axum::extract::{Query, State};
use axum::response::Response;
use axum::routing::{get, post};
use axum::{Json, Router};
use lambda_http::http::StatusCode;
use serde::Deserialize;
use serde_json::json;
use shared::domain::types::id::{InvitationId, UserId};
use shared::infra::{error_response, success_response};

use crate::domain::error::UserError::{InvalidUserSearchCriteria, InvitationError};
use crate::domain::port::user_command_handler::UserCommandHandler;
use crate::domain::port::user_service::UserService;
use crate::UserHandlerState;

pub fn user_router<UserCommandHandlerI, UserServiceI>() -> Router<UserHandlerState<UserCommandHandlerI, UserServiceI>>
where
    UserCommandHandlerI: UserCommandHandler + 'static,
    UserServiceI: UserService + 'static,
{
    Router::new()
        .route("/users", get(get_user_by_handler))
        .route("/users/register", post(register_user_handler))
}

#[derive(Deserialize)]
struct GetUserQuery {
    user_id: Option<UserId>,
    email: Option<String>,
}

async fn get_user_by_handler<UserCommandHandlerI, UserServiceI>(
    State(state): State<UserHandlerState<UserCommandHandlerI, UserServiceI>>,
    Query(params): Query<GetUserQuery>,
) -> Response
where
    UserCommandHandlerI: UserCommandHandler + 'static,
    UserServiceI: UserService + 'static,
{
    match state.user_service.get_user_by(params.user_id, params.email).await {
        Ok(user) => success_response(StatusCode::OK, user),
        Err(InvalidUserSearchCriteria) => error_response(StatusCode::BAD_REQUEST, InvalidUserSearchCriteria.into()),

        Err(unknown_error) => error_response(StatusCode::INTERNAL_SERVER_ERROR, unknown_error.into()),
    }
}

#[derive(Deserialize)]
struct RegisterUserRequest {
    invitation_id: InvitationId,
    password: String,
}

async fn register_user_handler<UserCommandHandlerI, UserServiceI>(
    State(state): State<UserHandlerState<UserCommandHandlerI, UserServiceI>>,
    Json(RegisterUserRequest { invitation_id, password }): Json<RegisterUserRequest>,
) -> Response
where
    UserCommandHandlerI: UserCommandHandler,
    UserServiceI: UserService,
{
    match state.command_handler.handle_register_user(invitation_id, password).await {
        Ok(_) => success_response(StatusCode::OK, json!({ "message": "User has been created" })),
        Err(InvitationError(error)) => error_response(StatusCode::BAD_REQUEST, error.into()),

        Err(unknown_error) => error_response(StatusCode::INTERNAL_SERVER_ERROR, unknown_error.into()),
    }
}
