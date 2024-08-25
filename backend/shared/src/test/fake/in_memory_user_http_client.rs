use std::sync::{Arc, Mutex};

use crate::domain::port::user_http_client::{Invitation, User, UserHttpClient};
use crate::domain::types::id::{FleetId, InvitationId, UserId};
use crate::infra::http::error::HttpClientError;
use crate::infra::time::TimeProvider;
use async_trait::async_trait;
use chrono::Duration;

#[derive(Clone)]
pub struct InMemoryUserHttpClient<TimeProviderI>
where
    TimeProviderI: TimeProvider,
{
    time_provider: TimeProviderI,
    users: Arc<Mutex<Vec<User>>>,
    invitations: Arc<Mutex<Vec<Invitation>>>,
}

impl<TimeProviderI> InMemoryUserHttpClient<TimeProviderI>
where
    TimeProviderI: TimeProvider,
{
    pub fn new(time_provider: TimeProviderI) -> Self {
        InMemoryUserHttpClient {
            time_provider,
            users: Arc::new(Mutex::new(Vec::new())),
            invitations: Arc::new(Mutex::new(Vec::new())),
        }
    }

    pub fn insert_user(&self, email: String) -> UserId {
        let user_id = UserId::default();

        let user = User {
            user_id: user_id.clone(),
            email: email.clone(),
        };

        self.users.lock().unwrap().push(user);

        user_id
    }

    pub fn insert_invitation(&self, fleet_id: FleetId, email: String) -> InvitationId {
        let invitation_id = InvitationId::default();

        let invitation = Invitation {
            invitation_id: invitation_id.clone(),
            fleet_id,
            email,
            due_at: self.time_provider.now() + Duration::days(7),
            created_at: self.time_provider.now(),
            accepted_at: None,
        };

        self.invitations.lock().unwrap().push(invitation);

        invitation_id
    }
}

#[async_trait]
impl<TimeProviderI> UserHttpClient for InMemoryUserHttpClient<TimeProviderI>
where
    TimeProviderI: TimeProvider,
{
    async fn get_user_by_email(&self, email: String) -> Result<Option<User>, HttpClientError> {
        let users = self.users.lock().unwrap();
        let user = users.iter().find(|user| user.email == email);

        Ok(user.cloned())
    }

    async fn get_invitation_by_email(&self, email: String) -> Result<Option<Invitation>, HttpClientError> {
        let invitations = self.invitations.lock().unwrap();
        let invitation = invitations.iter().find(|invitation| invitation.email == email);

        Ok(invitation.cloned())
    }
}
