use crate::domain::error::UserError;
use async_trait::async_trait;
use auto_impl::auto_impl;
use shared::domain::event::users::UserRegisteredPayload;

#[async_trait]
#[auto_impl(Arc)]
pub trait UserProjectionHandler: Clone + Sync + Send {
    async fn handle_registered_user(&self, payload: UserRegisteredPayload) -> Result<(), UserError>;
}
