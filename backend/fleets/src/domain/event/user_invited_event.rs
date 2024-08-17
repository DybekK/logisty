use serde::{Deserialize, Serialize};

use shared::domain::types::id::FleetId;
use shared::infra::sns::sns_client::SNSMessage;

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct UserInvitedEvent {
    pub event_type: String,
    pub payload: UserInvitedEventPayload,
}

#[derive(Clone, Debug, Serialize, Deserialize)]
pub struct UserInvitedEventPayload {
    pub fleet_id: FleetId,
    pub first_name: String,
    pub last_name: String,
    pub email: String,
}

impl SNSMessage for UserInvitedEvent {}

impl UserInvitedEvent {
    pub fn new(fleet_id: FleetId, first_name: String, last_name: String, email: String) -> Self {
        UserInvitedEvent {
            event_type: "user_invited".to_string(),
            payload: UserInvitedEventPayload {
                fleet_id,
                first_name,
                last_name,
                email,
            },
        }
    }
}
