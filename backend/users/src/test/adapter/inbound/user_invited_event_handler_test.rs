#[cfg(test)]
mod tests {
    use crate::adapter::inbound::user_invited_event_handler::UserInvitedEventHandler;
    use crate::domain::service::invitation_service_impl::InvitationServiceImpl;
    use crate::test::fake::in_memory_invitation_repository::InMemoryInvitationRepository;
    use shared::domain::event::UserInvitedPayload;
    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use std::sync::Arc;

    type InvitationRepositoryArc = Arc<InMemoryInvitationRepository>;
    type InvitationServiceArc = Arc<InvitationServiceImpl<InvitationRepositoryArc>>;
    type UserInvitedEventHandlerArc = Arc<UserInvitedEventHandler<InvitationServiceArc>>;

    fn setup() -> (InvitationRepositoryArc, InvitationServiceArc, UserInvitedEventHandlerArc) {
        let invitation_repository = Arc::new(InMemoryInvitationRepository::new());
        let invitation_service = Arc::new(InvitationServiceImpl::new(invitation_repository.clone()));
        let handler = Arc::new(UserInvitedEventHandler::new(invitation_service.clone()));

        (invitation_repository, invitation_service, handler)
    }

    #[tokio::test]
    async fn should_consume_event() {
        // given
        let (_, _, handler) = setup();

        let payload = UserInvitedPayload {
            fleet_id: FleetId::default(),
            role: Admin,
            first_name: "John".to_string(),
            last_name: "Doe".to_string(),
            email: "john.doe@example.com".to_string(),
        };

        // when
        let result = handler.handle(payload).await;

        // then
        assert!(result.is_ok());
    }
}
