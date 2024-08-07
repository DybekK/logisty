use std::error::Error;

use async_trait::async_trait;
use chrono::Utc;
use sqlx::PgPool;

use crate::domain::model::User;
use crate::domain::port::user_repository::UserRepository;
use crate::domain::UserId;

pub struct UserRepositoryImpl {
    pool: PgPool,
}

impl UserRepositoryImpl {
    pub fn new(pool: PgPool) -> UserRepositoryImpl {
        UserRepositoryImpl { pool }
    }
}

#[async_trait]
impl UserRepository for UserRepositoryImpl {
    async fn find_by_id(&self, id: UserId) -> Result<Option<User>, Box<dyn Error>> {
        let user = sqlx::query_as::<_, User>(r#"SELECT * FROM users WHERE id = $1"#)
            .bind(id)
            .fetch_optional(&self.pool)
            .await?;

        Ok(user)
    }

    async fn insert(&self, email: String, password: String) -> Result<UserId, Box<dyn Error>> {
        let id = UserId::default();
        let created_at = Utc::now();
        let updated_at = created_at;

        sqlx::query(r#"INSERT INTO users (id, email, password, created_at, updated_at) VALUES ($1, $2, $3, $4, $5)"#)
            .bind(id.clone())
            .bind(email)
            .bind(password)
            .bind(created_at)
            .bind(updated_at)
            .execute(&self.pool)
            .await?;

        Ok(id)
    }
}
