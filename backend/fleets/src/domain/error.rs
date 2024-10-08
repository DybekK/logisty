use thiserror::Error;

use shared::infra::database::error::DatabaseError;
use shared::infra::http::error::HttpClientError;
use shared::infra::queue::error::SNSError;

#[derive(Error, Debug)]
#[non_exhaustive]
pub enum MemberInvitationError {
    #[error("Member of given email has been already invited")]
    InvitationAlreadyExists,
    
    #[error("Member of given email already exists")]
    MemberAlreadyExists,

    #[error("Fleet of given id does not exist")]
    FleetNotExists,

    #[error(transparent)]
    DatabaseError(#[from] DatabaseError),

    #[error(transparent)]
    SNSError(#[from] SNSError),

    #[error(transparent)]
    HttpClientError(#[from] HttpClientError),
}

#[derive(Error, Debug)]
#[non_exhaustive]
pub enum FleetError {
    #[error("Fleet of given name already exists")]
    FleetAlreadyExists,

    #[error(transparent)]
    DatabaseError(#[from] DatabaseError),
}
