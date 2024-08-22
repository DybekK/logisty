use chrono::NaiveDateTime;
use serde::{Deserialize, Serialize};
use sqlx::FromRow;

use shared::domain::types::id::{FleetId, InvitationId, UserId};
use shared::domain::types::Role;
use shared::infra::time::TimeProvider;

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize, FromRow)]
pub struct User {
    pub user_id: UserId,
    pub fleet_id: FleetId,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub password: String,
    pub role: Role,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize, FromRow)]
pub struct Invitation {
    pub invitation_id: InvitationId,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub fleet_id: FleetId,
    pub role: Role,
    pub due_at: NaiveDateTime,
    pub created_at: NaiveDateTime,
    pub accepted_at: Option<NaiveDateTime>,
}

impl Invitation {
    pub fn is_active<TimeProviderI: TimeProvider>(&self, time_provider: &TimeProviderI) -> bool {
        self.accepted_at.is_none() && self.due_at > time_provider.now()
    }
}
