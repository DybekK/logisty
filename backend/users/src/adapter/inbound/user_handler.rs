extern crate serde_json;

use axum::extract::{Query, State};
use axum::response::Response;
use axum::Router;
use axum::routing::get;
use lambda_http::http::StatusCode;
use serde::Deserialize;

use shared::domain::types::id::UserId;
use shared::infra::{error_response, success_response};

use crate::domain::error::UserError::InvalidUserSearchCriteria;
use crate::domain::port::user_service::UserService;
use crate::UserHandlerState;

pub fn user_router<UserServiceI>() -> Router<UserHandlerState<UserServiceI>>
where
    UserServiceI: UserService + 'static,
{
    Router::new().route("/users", get(get_user_by_handler::<UserServiceI>))
}

#[derive(Deserialize)]
struct GetUserQuery {
    user_id: Option<UserId>,
    email: Option<String>,
}

async fn get_user_by_handler<UserServiceI>(
    State(state): State<UserHandlerState<UserServiceI>>,
    Query(params): Query<GetUserQuery>,
) -> Response
where
    UserServiceI: UserService,
{
    match state.user_service.get_user_by(params.user_id, params.email).await {
        Ok(user) => success_response(StatusCode::OK, user),
        Err(InvalidUserSearchCriteria) => error_response(StatusCode::BAD_REQUEST, InvalidUserSearchCriteria.into()),
        
        Err(unknown_error) => error_response(StatusCode::INTERNAL_SERVER_ERROR, unknown_error.into()),
    }
}
