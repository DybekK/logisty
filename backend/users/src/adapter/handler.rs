use crate::adapter::dto::RegisterNewUser;
use crate::domain::port::UserService;
use lambda_http::http::StatusCode;
use lambda_http::{Request, RequestPayloadExt, Response};
use serde_json::json;
use std::error::Error;

extern crate serde_json;

pub async fn register_user_handler<'a, T: UserService>(
    user_service: &'a T,
    request: Request,
) -> Result<Response<String>, Box<dyn Error>> {
    let body = request.payload::<RegisterNewUser>()?;

    match body {
        Some(user) => {
            let user_id = user_service.register_user(user.email, user.password).await?;
            let response = json_response(StatusCode::OK, json!({ "user_id": user_id }).to_string());
            Ok(response)
        }
        None => {
            let response = json_response(
                StatusCode::BAD_REQUEST,
                json!({ "error": "Invalid request body" }).to_string(),
            );
            Ok(response)
        }
    }
}

fn json_response(status_code: StatusCode, body: String) -> Response<String> {
    Response::builder()
        .status(status_code)
        .header("Content-Type", "application/json")
        .body(body)
        .unwrap()
}
