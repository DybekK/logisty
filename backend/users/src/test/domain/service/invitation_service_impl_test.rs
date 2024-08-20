#[cfg(test)]
mod tests {
    use crate::domain::port::invitation_repository::InvitationRepository;
    use crate::domain::port::invitation_service::InvitationService;
    use crate::domain::service::invitation_service_impl::InvitationServiceImpl;
    use crate::test::fake::in_memory_invitation_repository::InMemoryInvitationRepository;

    use crate::domain::model::InvitationStatus::Pending;
    use shared::domain::types::id::FleetId;
    use shared::domain::types::Role::Admin;
    use std::sync::Arc;

    type InvitationRepositoryArc = Arc<InMemoryInvitationRepository>;
    type InvitationServiceArc = Arc<InvitationServiceImpl<InvitationRepositoryArc>>;

    fn setup() -> (InvitationRepositoryArc, InvitationServiceArc) {
        let invitation_repository = Arc::new(InMemoryInvitationRepository::new());
        let invitation_service = Arc::new(InvitationServiceImpl::new(invitation_repository.clone()));

        (invitation_repository, invitation_service)
    }

    #[tokio::test]
    async fn should_create_invitation() {
        // given
        let (invitation_repository, invitation_service) = setup();

        let fleet_id = FleetId::default();
        let role = Admin;
        let first_name = "John".to_string();
        let last_name = "Doe".to_string();
        let email = "john.doe@example.com".to_string();

        // when
        let invitation_id = invitation_service
            .create_invitation(
                fleet_id.clone(),
                role.clone(),
                first_name.clone(),
                last_name.clone(),
                email.clone(),
            )
            .await
            .unwrap();

        let invitation = invitation_repository
            .find_by_id(invitation_id.clone())
            .await
            .unwrap()
            .unwrap();

        // then
        assert_eq!(invitation.invitation_id, invitation_id);
        assert_eq!(invitation.email, email);
        assert_eq!(invitation.fleet_id, fleet_id);
        assert_eq!(invitation.first_name, first_name);
        assert_eq!(invitation.last_name, last_name);
        assert!(matches!(invitation.role, Admin));
        assert!(matches!(invitation.status, Pending));
        assert!(invitation.accepted_at.is_none());
        assert!(invitation.denied_at.is_none());
        assert!(invitation.due_at > invitation.created_at);
    }
}
