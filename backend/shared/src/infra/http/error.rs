use thiserror::Error;

#[derive(Debug, Error)]
pub enum HttpClientError {
    #[error(transparent)]
    RequestError(#[from] reqwest_middleware::Error),

    #[error(transparent)]
    JsonError(#[from] reqwest::Error),
}