use async_trait::async_trait;
use chrono::NaiveDateTime;
use sqlx::PgPool;

use shared::domain::types::id::{FleetId, UserId};
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;

use crate::domain::model::User;
use crate::domain::port::user_repository::UserRepository;

#[derive(Clone)]
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
    async fn find_by_id(&self, user_id: UserId) -> Result<Option<User>, DatabaseError> {
        let user = sqlx::query_as::<_, User>(r#"SELECT * FROM users WHERE user_id = $1"#)
            .bind(user_id)
            .fetch_optional(&self.pool)
            .await?;

        Ok(user)
    }

    async fn find_by_email(&self, email: String) -> Result<Option<User>, DatabaseError> {
        let user = sqlx::query_as::<_, User>(r#"SELECT * FROM users WHERE email = $1"#)
            .bind(email)
            .fetch_optional(&self.pool)
            .await?;

        Ok(user)
    }

    async fn insert(
        &self,
        fleet_id: FleetId,
        first_name: String,
        last_name: String,
        email: String,
        password: String,
        role: Role,
        created_at: NaiveDateTime,
    ) -> Result<UserId, DatabaseError> {
        let user_id = UserId::default();
        let updated_at = created_at;

        sqlx::query(
            r#"
            INSERT INTO users (user_id, fleet_id, first_name, last_name, email, password, role, created_at, updated_at)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
            "#,
        )
        .bind(user_id.clone())
        .bind(fleet_id)
        .bind(first_name)
        .bind(last_name)
        .bind(email)
        .bind(password)
        .bind(role)
        .bind(created_at)
        .bind(updated_at)
        .execute(&self.pool)
        .await?;

        Ok(user_id)
    }
}
