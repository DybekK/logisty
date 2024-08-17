use thiserror::Error;

use shared::infra::database::error::DatabaseError;

#[derive(Error, Debug)]
#[non_exhaustive]
pub enum UserError {
    #[error("Invalid user search criteria provided")]
    InvalidUserSearchCriteria,
    
    #[error(transparent)]
    DatabaseError(#[from] DatabaseError),
}
