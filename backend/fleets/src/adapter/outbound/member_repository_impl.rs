use async_trait::async_trait;
use chrono::Utc;
use sqlx::PgPool;

use shared::domain::types::id::{FleetId, FleetMemberId, UserId};
use shared::infra::database::error::DatabaseError;

use crate::domain::port::member_repository::MemberRepository;

#[derive(Clone)]
pub struct MemberRepositoryImpl {
    pool: PgPool,
}

impl MemberRepositoryImpl {
    pub fn new(pool: PgPool) -> MemberRepositoryImpl {
        MemberRepositoryImpl { pool }
    }
}

#[async_trait]
impl MemberRepository for MemberRepositoryImpl {
    async fn insert(&self, fleet_id: FleetId, user_id: UserId) -> Result<FleetMemberId, DatabaseError> {
        let id = FleetMemberId::default();
        let created_at = Utc::now();
        let updated_at = created_at;

        sqlx::query(r#"INSERT INTO fleet_members (fleet_member_id, fleet_id, user_id, updated_at, created_at) VALUES ($1, $2, $3, $4, $5)"#)
            .bind(id.clone())
            .bind(fleet_id)
            .bind(user_id)
            .bind(created_at)
            .bind(updated_at)
            .execute(&self.pool)
            .await?;

        Ok(id)
    }
}
