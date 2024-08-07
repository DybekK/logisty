pub mod error {
    #[derive(thiserror::Error, Debug)]
    #[error(transparent)]
    pub struct DatabaseError(#[from] pub sqlx::Error);
}
