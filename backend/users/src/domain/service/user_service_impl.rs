use async_trait::async_trait;
use std::error::Error;
use crate::domain::model::User;
use crate::domain::port::user_repository::UserRepository;
use crate::domain::port::user_service::UserService;
use crate::domain::UserId;

pub struct UserServiceImpl<T: UserRepository> {
    repository: T,
}

impl<T: UserRepository + Sync + Send> UserServiceImpl<T> {
    pub fn new(repository: T) -> UserServiceImpl<T> {
        UserServiceImpl { repository }
    }
}

#[async_trait]
impl<T: UserRepository + Sync + Send> UserService for UserServiceImpl<T> {
    async fn get_user(&self, id: UserId) -> Result<Option<User>, Box<dyn Error>> {
        self.repository.find_by_id(id).await
    }
    
    async fn register_user(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>> {
        self.repository.insert(email, password).await
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::sync::Arc;

    use crate::domain::port::user_service::UserService;
    use crate::test::fake::in_memory_user_repository::InMemoryUserRepository;

    type UserRepositoryArc = Arc<InMemoryUserRepository>;
    type UserServiceArc = Arc<UserServiceImpl<UserRepositoryArc>>;

    async fn setup() -> (UserRepositoryArc, UserServiceArc) {
        let user_repository = Arc::new(InMemoryUserRepository::new());
        let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

        (user_repository, user_service)
    }

    #[tokio::test]
    async fn should_register_and_retrieve_user_successfully() {
        let (user_repository, user_service) = setup().await;

        let email = "john@gmail.com".to_string();
        let password = "password".to_string();

        let user_id = user_service.register_user(email.clone(), password.clone()).await.unwrap();
        let user = user_repository.find_by_id(user_id.clone()).await.unwrap().unwrap();

        assert_eq!(user.id, user_id);
        assert_eq!(user.email, email);
        assert_eq!(user.password, password);
    }
}
