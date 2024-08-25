use async_trait::async_trait;
use chrono::Utc;
use sqlx::PgPool;

use shared::domain::types::id::FleetId;
use shared::infra::database::error::DatabaseError;

use crate::domain::model::Fleet;
use crate::domain::port::fleet_repository::FleetRepository;

#[derive(Clone)]
pub struct FleetRepositoryImpl {
    pool: PgPool,
}

impl FleetRepositoryImpl {
    pub fn new(pool: PgPool) -> FleetRepositoryImpl {
        FleetRepositoryImpl { pool }
    }
}

#[async_trait]
impl FleetRepository for FleetRepositoryImpl {
    async fn find_by_id(&self, fleet_id: FleetId) -> Result<Option<Fleet>, DatabaseError> {
        let user = sqlx::query_as::<_, Fleet>(r#"SELECT * FROM fleets WHERE fleet_id = $1"#)
            .bind(fleet_id)
            .fetch_optional(&self.pool)
            .await?;

        Ok(user)
    }

    async fn find_by_name(&self, fleet_name: String) -> Result<Option<Fleet>, DatabaseError> {
        let user = sqlx::query_as::<_, Fleet>(r#"SELECT * FROM fleets WHERE fleet_name = $1"#)
            .bind(fleet_name)
            .fetch_optional(&self.pool)
            .await?;

        Ok(user)
    }

    async fn insert(&self, fleet_name: String) -> Result<FleetId, DatabaseError> {
        let fleet_id = FleetId::default();
        let created_at = Utc::now();
        let updated_at = created_at;

        sqlx::query(r#"INSERT INTO fleets (fleet_id, fleet_name, created_at, updated_at) VALUES ($1, $2, $3, $4)"#)
            .bind(fleet_id.clone())
            .bind(fleet_name)
            .bind(created_at)
            .bind(updated_at)
            .execute(&self.pool)
            .await?;

        Ok(fleet_id)
    }
}
