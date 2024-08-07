use axum::routing::get;
use axum::{Json, Router};
use serde_json::{json, Value};

pub fn health_router<S: Clone + Send + Sync + 'static>(service_name: &'static str) -> Router<S> {
    Router::new().route("/health", get(|| health_handler(service_name)))
}

async fn health_handler(service_name: &str) -> Json<Value> {
    Json(json!({"status": "healthy", "service": service_name}))
}
