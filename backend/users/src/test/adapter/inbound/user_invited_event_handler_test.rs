#[cfg(test)]
mod tests {
    use crate::adapter::inbound::user_invited_event_handler::UserInvitedEventHandler;
    use crate::domain::service::invitation_service_impl::InvitationServiceImpl;
    use crate::test::fake::in_memory_invitation_repository::InMemoryInvitationRepository;
    use shared::domain::event::users::UserInvitedPayload;
    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use shared::infra::time::{FakeTimeProvider, TimeProvider};
    use std::sync::Arc;

    type TimeProviderArc = Arc<FakeTimeProvider>;

    type InvitationRepositoryArc = Arc<InMemoryInvitationRepository>;

    type InvitationServiceArc = Arc<InvitationServiceImpl<TimeProviderArc, InvitationRepositoryArc>>;

    type UserInvitedEventHandlerArc = Arc<UserInvitedEventHandler<InvitationServiceArc>>;

    struct TestDependencies {
        pub time_provider: TimeProviderArc,

        pub handler: UserInvitedEventHandlerArc,
    }

    fn setup() -> TestDependencies {
        let time_provider = Arc::new(FakeTimeProvider::new());
        // Repositories
        let invitation_repository = Arc::new(InMemoryInvitationRepository::new());

        // Services
        let invitation_service = Arc::new(InvitationServiceImpl::new(
            time_provider.clone(),
            invitation_repository.clone(),
        ));

        // Handler
        let handler = Arc::new(UserInvitedEventHandler::new(invitation_service.clone()));

        TestDependencies { time_provider, handler }
    }

    #[tokio::test]
    async fn should_consume_event() {
        // given
        let TestDependencies {
            time_provider, handler, ..
        } = setup();

        let payload = UserInvitedPayload {
            fleet_id: FleetId::default(),
            role: Admin,
            first_name: "John".to_string(),
            last_name: "Doe".to_string(),
            email: "john.doe@example.com".to_string(),
            created_at: time_provider.now(),
        };

        // when
        let result = handler.handle(payload).await;

        // then
        assert!(result.is_ok());
    }
}
