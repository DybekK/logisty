use crate::domain::event::Event::UserInvited;
use crate::domain::types::id::FleetId;
use crate::domain::types::Role;
use crate::infra::queue::sns_client::SNSMessage;
use serde::{Deserialize, Serialize};

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct UserInvitedPayload {
    pub fleet_id: FleetId,
    pub role: Role,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
}

#[derive(Clone, Debug, Serialize, Deserialize)]
#[serde(tag = "event_type", content = "payload")]
pub enum Event {
    UserInvited(UserInvitedPayload),
}

impl SNSMessage for Event {}

impl Event {
    pub fn user_invited(fleet_id: FleetId, role: Role, first_name: String, last_name: String, email: String) -> Self {
        UserInvited(UserInvitedPayload {
            fleet_id,
            role,
            first_name,
            last_name,
            email,
        })
    }
}
