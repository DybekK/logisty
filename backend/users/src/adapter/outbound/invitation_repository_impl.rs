use async_trait::async_trait;
use chrono::{Duration, Utc};
use sqlx::PgPool;

use crate::domain::model::Invitation;
use crate::domain::model::InvitationStatus::Pending;
use crate::domain::port::invitation_repository::InvitationRepository;
use shared::domain::types::id::{FleetId, InvitationId};
use shared::domain::types::Role;
use shared::infra::database::error::DatabaseError;

#[derive(Clone)]
pub struct InvitationRepositoryImpl {
    pool: PgPool,
}

impl InvitationRepositoryImpl {
    pub fn new(pool: PgPool) -> InvitationRepositoryImpl {
        InvitationRepositoryImpl { pool }
    }
}

#[async_trait]
impl InvitationRepository for InvitationRepositoryImpl {
    async fn find_by_id(&self, invitation_id: InvitationId) -> Result<Option<Invitation>, DatabaseError> {
        let user = sqlx::query_as::<_, Invitation>(r#"SELECT * FROM invitations WHERE invitation_id = $1"#)
            .bind(invitation_id)
            .fetch_optional(&self.pool)
            .await?;

        Ok(user)
    }

    async fn insert(
        &self,
        fleet_id: FleetId,
        role: Role,
        first_name: String,
        last_name: String,
        email: String,
    ) -> Result<InvitationId, DatabaseError> {
        let invitation_id = InvitationId::default();
        let status = Pending;
        let created_at = Utc::now();
        let due_at = created_at.clone() + Duration::days(7);

        sqlx::query(r#"INSERT INTO invitations (invitation_id, first_name, last_name, email, fleet_id, role, status, due_at, created_at) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)"#) 
        .bind(invitation_id.clone())
        .bind(first_name)
        .bind(last_name)
        .bind(email)
        .bind(fleet_id)
        .bind(role)
        .bind(status)
        .bind(due_at)
        .bind(created_at)
        .execute(&self.pool)
        .await?;

        Ok(invitation_id)
    }
}
