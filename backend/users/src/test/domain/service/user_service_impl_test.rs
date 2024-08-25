#[cfg(test)]
mod tests {
    use crate::domain::error::UserError::InvalidUserSearchCriteria;
    use crate::domain::model::User;
    use crate::domain::port::user_repository::UserRepository;
    use crate::domain::port::user_service::UserService;
    use crate::domain::service::user_service_impl::UserServiceImpl;
    use crate::test::fake::in_memory_user_repository::InMemoryUserRepository;
    use chrono::NaiveDateTime;
    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use shared::infra::time::{FakeTimeProvider, TimeProvider};
    use std::sync::Arc;

    type TimeProviderArc = Arc<FakeTimeProvider>;

    type UserRepositoryArc = Arc<InMemoryUserRepository>;

    type UserServiceArc = Arc<UserServiceImpl<UserRepositoryArc>>;

    struct TestDependencies {
        pub time_provider: TimeProviderArc,

        pub user_repository: UserRepositoryArc,

        pub user_service: UserServiceArc,
    }

    fn setup() -> TestDependencies {
        let time_provider = Arc::new(FakeTimeProvider::new());

        // Repositories
        let user_repository = Arc::new(InMemoryUserRepository::new());

        // Services
        let user_service = Arc::new(UserServiceImpl::new(user_repository.clone()));

        TestDependencies {
            time_provider,
            user_repository,
            user_service,
        }
    }

    // get_user_by

    #[tokio::test]
    async fn should_find_user_by() {
        let parameters = vec![(true, false), (false, true)];

        for (use_id, use_email) in parameters {
            println!("use_id: {}, use_email: {}", use_id, use_email);

            // given
            let TestDependencies {
                time_provider,
                user_repository,
                user_service,
                ..
            } = setup();

            let fleet_id = FleetId::default();
            let first_name = "John".to_string();
            let last_name = "Doe".to_string();
            let email = "john.doe@gmail.com".to_string();
            let password = "password".to_string();
            let role = Admin;
            let created_at = time_provider.now();

            let user_id = user_repository
                .insert(
                    fleet_id.clone(),
                    first_name.clone(),
                    last_name.clone(),
                    email.clone(),
                    password.clone(),
                    role.clone(),
                    created_at,
                )
                .await
                .unwrap();

            // when
            let user = user_service
                .get_user_by(
                    if use_id { Some(user_id.clone()) } else { None },
                    if use_email { Some(email.clone()) } else { None },
                )
                .await
                .unwrap()
                .unwrap();

            // then
            let expected_user = User {
                user_id,
                fleet_id,
                first_name,
                last_name,
                email,
                role,
                password,
                created_at: NaiveDateTime::default(),
                updated_at: NaiveDateTime::default(),
            };

            assert_eq!(user, expected_user);
        }
    }

    #[tokio::test]
    async fn should_return_error_when_search_criteria_is_invalid() {
        // given
        let TestDependencies { user_service, .. } = setup();

        // when
        let result = user_service.get_user_by(None, None).await.unwrap_err();

        // then
        assert!(matches!(result, InvalidUserSearchCriteria));
    }

    // register_user

    #[tokio::test]
    async fn should_register_user() {
        // given
        let TestDependencies {
            time_provider,
            user_repository,
            user_service,
            ..
        } = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@gmail.com".to_string();
        let password = "password".to_string();
        let created_at = time_provider.now();

        // when

        let user_id = user_service
            .register_user(
                fleet_id.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
                password.clone(),
                role.clone(),
                created_at.clone(),
            )
            .await
            .unwrap();

        // then
        let user = user_repository.find_by_id(user_id.clone()).await.unwrap().unwrap();

        let expected_user = User {
            user_id,
            fleet_id,
            first_name,
            last_name,
            email,
            role,
            password,
            created_at: NaiveDateTime::default(),
            updated_at: NaiveDateTime::default(),
        };

        assert_eq!(user, expected_user);
    }
}
