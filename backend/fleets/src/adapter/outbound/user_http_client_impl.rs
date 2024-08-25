use async_trait::async_trait;
use reqwest_middleware::ClientWithMiddleware;

use shared::domain::port::user_http_client::{Invitation, User, UserHttpClient};
use shared::infra::http::error::HttpClientError;
use shared::infra::http::http_client::new_client;

#[derive(Clone)]
pub struct UserHttpClientImpl {
    base_url: String,
    client: ClientWithMiddleware,
}

impl UserHttpClientImpl {
    pub fn new(base_url: String) -> Self {
        UserHttpClientImpl {
            base_url,
            client: new_client(),
        }
    }
}

#[async_trait]
impl UserHttpClient for UserHttpClientImpl {
    async fn get_user_by_email(&self, email: String) -> Result<Option<User>, HttpClientError> {
        let url = format!("{}/users", self.base_url);
        let params = [("email", email)];

        let response = self.client.get(&url).query(&params).send().await?;

        Ok(response.json::<Option<User>>().await?)
    }

    async fn get_invitation_by_email(&self, email: String) -> Result<Option<Invitation>, HttpClientError> {
        let url = format!("{}/invitations", self.base_url);
        let params = [("email", email)];

        let response = self.client.get(&url).query(&params).send().await?;

        Ok(response.json::<Option<Invitation>>().await?)
    }
}
