use crate::domain::UserId;
use async_trait::async_trait;
use chrono::Utc;
use sqlx::PgPool;
use std::error::Error;

#[async_trait]
pub trait UserRepository {
    async fn insert(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>>;
}

pub struct UserRepositoryImpl<'a> {
    pool: &'a PgPool,
}

impl UserRepositoryImpl<'_> {
    pub fn new(pool: &PgPool) -> UserRepositoryImpl {
        UserRepositoryImpl { pool }
    }
}

#[async_trait]
impl UserRepository for UserRepositoryImpl<'_> {
    async fn insert(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>> {
        let id = UserId::new();
        let created_at = Utc::now();
        let updated_at = created_at.clone();

        sqlx::query!(
            r#"INSERT INTO users (id, email, password, created_at, updated_at) VALUES ($1, $2, $3, $4, $5)"#,
            id.0,
            email,
            password,
            created_at,
            updated_at
        )
        .execute(self.pool)
        .await?;

        Ok(id)
    }
}
