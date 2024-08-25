use crate::domain::event::users::{UserInvitedPayload, UserRegisteredPayload};
use crate::infra::queue::sns_client::SerializableMessage;
use serde::{Deserialize, Serialize};

pub mod users;

#[derive(Clone, Debug, Serialize, Deserialize)]
#[serde(tag = "event_type", content = "payload")]
pub enum Event {
    // users events
    UserInvited(UserInvitedPayload),
    UserRegistered(UserRegisteredPayload),
}

impl SerializableMessage for Event {}
