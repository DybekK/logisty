use thiserror::Error;

#[derive(Debug, Error)]
pub enum HttpClientError {
    #[error(transparent)]
    RequestError(#[from] reqwest::Error),

    #[error(transparent)]
    JsonError(#[from] serde_json::Error),
}