use async_trait::async_trait;
use std::error::Error;
use std::sync::{Arc, Mutex};

use crate::domain::model::User;
use crate::domain::port::user_repository::UserRepository;
use crate::domain::UserId;

pub struct InMemoryUserRepository {
    pub users: Mutex<Vec<User>>,
}

impl InMemoryUserRepository {
    pub fn new() -> Self {
        InMemoryUserRepository {
            users: Mutex::new(Vec::new()),
        }
    }
}

#[async_trait]
impl UserRepository for Arc<InMemoryUserRepository> {
    async fn find_by_id(&self, id: UserId) -> Result<Option<User>, Box<dyn Error>> {
        let users = self.users.lock().unwrap();
        let user = users.iter().find(|user| user.id == id).cloned();

        Ok(user)
    }

    async fn insert(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>> {
        let id = UserId::default();
        let user = User {
            id: id.clone(),
            email,
            password,
        };
        let mut users = self.users.lock().unwrap();
        users.push(user);

        Ok(id)
    }
}
