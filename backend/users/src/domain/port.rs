use crate::application::repository::UserRepository;
use crate::domain::UserId;
use async_trait::async_trait;
use std::error::Error;

#[async_trait]
pub trait UserService {
    async fn register_user(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>>;
}

pub struct UserServiceImpl<'a, T: UserRepository + Sync> {
    repository: &'a T,
}

impl<T: UserRepository + Sync> UserServiceImpl<'_, T> {
    pub fn new(repository: &T) -> UserServiceImpl<T> {
        UserServiceImpl { repository }
    }
}

#[async_trait]
impl<T: UserRepository + Sync> UserService for UserServiceImpl<'_, T> {
    async fn register_user(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>> {
        self.repository.insert(email, password).await
    }
}
