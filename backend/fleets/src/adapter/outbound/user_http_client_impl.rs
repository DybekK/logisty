use async_trait::async_trait;
use reqwest::{Client, StatusCode};

use shared::domain::port::user_http_client::{User, UserHttpClient};
use shared::infra::http::error::HttpClientError;

#[derive(Clone)]
pub struct UserHttpClientImpl {
    base_url: String,
    client: Client,
}

impl UserHttpClientImpl {
    pub fn new(base_url: String) -> Self {
        UserHttpClientImpl {
            base_url,
            client: Client::new(),
        }
    }
}

#[async_trait]
impl UserHttpClient for UserHttpClientImpl {
    async fn get_user_by_email(&self, email: String) -> Result<Option<User>, HttpClientError> {
        let url = format!("{}/users", self.base_url);
        let response = self.client.get(&url).form(&[("email", email)]).send().await?;

        match response.status() {
            StatusCode::NOT_FOUND => Ok(None),
            _ => Ok(response.json::<Option<User>>().await?),
        }
    }
}
