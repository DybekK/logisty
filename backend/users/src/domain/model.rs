use chrono::NaiveDateTime;
use serde::{Deserialize, Serialize};
use sqlx::{FromRow, Type};

use shared::domain::types::id::{FleetId, InvitationId, UserId};
use shared::domain::types::Role;

#[derive(Debug, Clone, Serialize, Deserialize, FromRow)]
pub struct User {
    pub user_id: UserId,
    pub email: String,
    pub password: String,
    pub role: Role,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Debug, Clone, Serialize, Deserialize, Type)]
#[sqlx(type_name = "VARCHAR")]
pub enum InvitationStatus {
    Pending,
    Accepted,
    Rejected,
}

#[derive(Debug, Clone, Serialize, Deserialize, FromRow)]
pub struct Invitation {
    pub invitation_id: InvitationId,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub fleet_id: FleetId,
    pub role: Role,
    pub status: InvitationStatus,
    pub due_at: NaiveDateTime,
    pub created_at: NaiveDateTime,
    pub accepted_at: Option<NaiveDateTime>,
    pub denied_at: Option<NaiveDateTime>,
}
