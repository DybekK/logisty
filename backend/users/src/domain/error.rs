use thiserror::Error;

use shared::infra::database::error::DatabaseError;
use shared::infra::queue::error::SNSError;

#[derive(Error, Debug)]
#[non_exhaustive]
pub enum UserError {
    #[error("Invalid user search criteria provided")]
    InvalidUserSearchCriteria,

    #[error(transparent)]
    InvitationError(#[from] InvitationError),

    #[error(transparent)]
    SNSError(#[from] SNSError),

    #[error(transparent)]
    DatabaseError(#[from] DatabaseError),
}

#[derive(Error, Debug)]
#[non_exhaustive]
pub enum InvitationError {
    #[error("Invalid invitation search criteria provided")]
    InvalidInvitationSearchCriteria,
    
    #[error("Invitation not found")]
    InvitationNotFound,

    #[error("Invitation is inactive")]
    InvitationInactive,

    #[error(transparent)]
    DatabaseError(#[from] DatabaseError),
}
