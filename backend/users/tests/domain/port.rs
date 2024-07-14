use users::application::repository::UserRepository;
use users::domain::port::{UserService, UserServiceImpl};
use crate::application::repository::InMemoryUserRepository;

#[tokio::test]
async fn register_user() {
    let user_repository = InMemoryUserRepository::new();
    let user_service = UserServiceImpl::new(&user_repository);

    let email = "john@gmail.com".to_string();
    let password = "password".to_string();

    let user_id = user_service.register_user(email.clone(), password.clone()).await.unwrap();
    let user = user_repository.find_by_id(user_id.clone()).await.unwrap().unwrap();

    assert_eq!(user.id, user_id);
    assert_eq!(user.email, email);
    assert_eq!(user.password, password);
}