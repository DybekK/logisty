use crate::domain::event::Event;
use crate::domain::event::Event::UserInvited;
use crate::domain::event::Event::UserRegistered;
use crate::domain::types::id::{FleetId, InvitationId};
use crate::domain::types::Role;
use chrono::NaiveDateTime;

use crate::infra::time::TimeProvider;
use serde::{Deserialize, Serialize};

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct UserInvitedPayload {
    pub fleet_id: FleetId,
    pub role: Role,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
    pub created_at: NaiveDateTime,
}

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct UserRegisteredPayload {
    pub invitation_id: InvitationId,
    pub fleet_id: FleetId,
    pub role: Role,
    pub first_name: String,
    pub last_name: String,
    pub password: String,
    pub email: String,
    pub accepted_at: NaiveDateTime,
}

pub fn user_invited<'a, TimeProviderI: TimeProvider>(
    time_provider: &'a TimeProviderI,
) -> impl Fn(FleetId, Role, String, String, String) -> Event + 'a {
    move |fleet_id, role, first_name, last_name, email| {
        UserInvited(UserInvitedPayload {
            fleet_id,
            role,
            first_name,
            last_name,
            email,
            created_at: time_provider.now(),
        })
    }
}

pub fn user_registered<'a, TimeProviderI: TimeProvider>(
    time_provider: &'a TimeProviderI,
) -> impl Fn(InvitationId, FleetId, Role, String, String, String, String) -> Event + 'a {
    move |invitation_id, fleet_id, role, first_name, last_name, password, email| {
        UserRegistered(UserRegisteredPayload {
            invitation_id,
            fleet_id,
            role,
            first_name,
            last_name,
            password,
            email,
            accepted_at: time_provider.now(),
        })
    }
}
