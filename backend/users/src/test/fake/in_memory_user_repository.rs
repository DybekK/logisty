use std::sync::{Arc, Mutex};

use async_trait::async_trait;
use chrono::NaiveDateTime;

use shared::domain::types::id::UserId;
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;

use crate::domain::model::User;
use crate::domain::port::user_repository::UserRepository;

#[derive(Clone)]
pub struct InMemoryUserRepository {
    users: Arc<Mutex<Vec<User>>>,
}

impl InMemoryUserRepository {
    pub fn new() -> Self {
        InMemoryUserRepository {
            users: Arc::new(Mutex::new(Vec::new())),
        }
    }
}

#[async_trait]
impl UserRepository for InMemoryUserRepository {
    async fn find_by_id(&self, user_id: UserId) -> Result<Option<User>, DatabaseError> {
        let users = self.users.lock().unwrap();
        let user = users.iter().find(|user| user.user_id == user_id).cloned();

        Ok(user)
    }

    async fn find_by_email(&self, email: String) -> Result<Option<User>, DatabaseError> {
        let users = self.users.lock().unwrap();
        let user = users.iter().find(|user| user.email == email).cloned();

        Ok(user)
    }

    async fn insert(&self, email: String, password: String, role: Role) -> Result<UserId, DatabaseError> {
        let mut users = self.users.lock().unwrap();

        let user_id = UserId::default();
        let user = User {
            user_id: user_id.clone(),
            email,
            password,
            role,
            created_at: NaiveDateTime::default(),
            updated_at: NaiveDateTime::default(),
        };
        users.push(user);

        Ok(user_id)
    }
}
