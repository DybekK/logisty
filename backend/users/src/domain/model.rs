use chrono::NaiveDateTime;
use serde::{Deserialize, Serialize};
use sqlx::{FromRow, Type};

use shared::domain::types::id::{FleetId, InvitationId, UserId};

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
pub enum Role {
    Admin,
    Dispatcher,
    Driver,
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
    pub email: String,
    pub fleet_id: FleetId,
    pub role: Role,
    pub status: InvitationStatus,
    pub created_at: NaiveDateTime,
    pub accepted_at: Option<NaiveDateTime>,
    pub denied_at: Option<NaiveDateTime>,
}
