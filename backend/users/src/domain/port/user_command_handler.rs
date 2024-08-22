use async_trait::async_trait;
use auto_impl::auto_impl;

use crate::domain::error::UserError;
use shared::domain::types::id::InvitationId;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserCommandHandler: Clone + Sync + Send {
    async fn handle_register_user(&self, invitation_id: InvitationId, password: String) -> Result<(), UserError>;
}
