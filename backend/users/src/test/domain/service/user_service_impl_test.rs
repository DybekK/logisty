#[cfg(test)]
mod tests {
    use crate::domain::error::UserError::InvalidUserSearchCriteria;
    use crate::domain::port::user_repository::UserRepository;
    use crate::domain::port::user_service::UserService;
    use crate::domain::service::user_service_impl::UserServiceImpl;
    use crate::test::fake::in_memory_user_repository::InMemoryUserRepository;
    use shared::domain::types::Role::Admin;
    use std::sync::Arc;

    type UserRepositoryArc = Arc<InMemoryUserRepository>;
    type UserServiceArc = Arc<UserServiceImpl<UserRepositoryArc>>;

    fn setup() -> (UserRepositoryArc, UserServiceArc) {
        let user_repository = Arc::new(InMemoryUserRepository::new());
        let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

        (user_repository, user_service)
    }

    #[tokio::test]
    async fn should_find_user_by_email() {
        // given
        let (user_repository, user_service) = setup();

        let email = "email@gmail.com".to_string();
        let password = "password".to_string();
        let role = Admin;

        let user_id = user_repository.insert(email.clone(), password, role).await.unwrap();

        // when
        let result = user_service.get_user_by(None, Some(email.clone())).await.unwrap().unwrap();

        // then
        assert_eq!(result.user_id, user_id);
        assert_eq!(result.email, email);
        assert!(matches!(result.role, Admin));
    }

    #[tokio::test]
    async fn should_find_user_by_id() {
        // given
        let (user_repository, user_service) = setup();

        let email = "email@gmail.com".to_string();
        let password = "password".to_string();
        let role = Admin;

        let user_id = user_repository.insert(email.clone(), password, role).await.unwrap();

        // when
        let result = user_service.get_user_by(Some(user_id.clone()), None).await.unwrap().unwrap();

        // then
        assert_eq!(result.user_id, user_id);
        assert_eq!(result.email, email);
        assert!(matches!(result.role, Admin));
    }

    #[tokio::test]
    async fn should_return_error_when_search_criteria_is_invalid() {
        // given
        let (_, user_service) = setup();

        // when
        let result = user_service.get_user_by(None, None).await.unwrap_err();

        // then
        assert!(matches!(result, InvalidUserSearchCriteria));
    }
}
