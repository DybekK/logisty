use async_trait::async_trait;

use shared::domain::types::id::UserId;
use UserError::InvalidUserSearchCriteria;

use crate::domain::error::UserError;
use crate::domain::model::User;
use crate::domain::port::user_repository::UserRepository;
use crate::domain::port::user_service::UserService;

#[derive(Clone)]
pub struct UserServiceImpl<UserRepositoryI>
where
    UserRepositoryI: UserRepository,
{
    user_repository: UserRepositoryI,
}

impl<UserRepositoryI> UserServiceImpl<UserRepositoryI>
where
    UserRepositoryI: UserRepository,
{
    pub fn new(user_repository: UserRepositoryI) -> UserServiceImpl<UserRepositoryI> {
        UserServiceImpl { user_repository }
    }
}

#[async_trait]
impl<UserRepositoryI> UserService for UserServiceImpl<UserRepositoryI>
where
    UserRepositoryI: UserRepository,
{
    async fn get_user_by(&self, user_id: Option<UserId>, email: Option<String>) -> Result<Option<User>, UserError> {
        match (user_id, email) {
            (Some(user_id), None) => Ok(self.user_repository.find_by_id(user_id).await?),
            (None, Some(email)) => Ok(self.user_repository.find_by_email(email).await?),
            _ => Err(InvalidUserSearchCriteria),
        }
    }
}
