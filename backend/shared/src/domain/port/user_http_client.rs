use async_trait::async_trait;
use auto_impl::auto_impl;
use chrono::NaiveDateTime;
use serde::{Deserialize, Serialize};

use crate::domain::types::id::{FleetId, InvitationId, UserId};
use crate::infra::http::error::HttpClientError;

#[derive(Clone, Serialize, Deserialize)]
pub struct User {
    pub user_id: UserId,
    pub email: String,
}

#[derive(Clone, Serialize, Deserialize)]
pub struct Invitation {
    pub invitation_id: InvitationId,
    pub fleet_id: FleetId,
    pub email: String,
    pub due_at: NaiveDateTime,
    pub created_at: NaiveDateTime,
    pub accepted_at: Option<NaiveDateTime>,
}

#[async_trait]
#[auto_impl(Arc)]
pub trait UserHttpClient: Clone + Send + Sync {
    async fn get_user_by_email(&self, email: String) -> Result<Option<User>, HttpClientError>;
    async fn get_invitation_by_email(&self, email: String) -> Result<Option<Invitation>, HttpClientError>;
}
