use thiserror::Error;
use shared::infra::database::error::DatabaseError;

#[derive(Error, Debug)]
#[non_exhaustive]
pub enum MemberInvitationError {
    #[error("Fleet of given email already exists")]
    MemberAlreadyExists,

    #[error(transparent)]
    DatabaseError(#[from] DatabaseError),
}

#[derive(Error, Debug)]
#[non_exhaustive]
pub enum FleetError {
    #[error("Fleet of given name already exists")]
    FleetAlreadyExists,
    
    #[error(transparent)]
    DatabaseError(#[from] DatabaseError),
}