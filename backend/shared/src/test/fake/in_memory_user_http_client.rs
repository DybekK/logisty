use std::sync::{Arc, Mutex};

use async_trait::async_trait;

use crate::domain::port::user_http_client::{User, UserHttpClient};
use crate::domain::types::id::UserId;
use crate::infra::http::error::HttpClientError;

#[derive(Clone)]
pub struct InMemoryUserHttpClient {
    users: Arc<Mutex<Vec<User>>>,
}

impl InMemoryUserHttpClient {
    pub fn new() -> Self {
        InMemoryUserHttpClient {
            users: Arc::new(Mutex::new(Vec::new())),
        }
    }

    pub fn insert(&self, email: String) -> UserId {
        let user_id = UserId::default();

        let user = User {
            user_id: user_id.clone(),
            email: email.clone(),
        };

        self.users.lock().unwrap().push(user);

        user_id
    }
}

#[async_trait]
impl UserHttpClient for InMemoryUserHttpClient {
    async fn get_user_by_email(&self, email: String) -> Result<Option<User>, HttpClientError> {
        let users = self.users.lock().unwrap();
        let user = users.iter().find(|user| user.email == email);

        Ok(user.cloned())
    }
}
