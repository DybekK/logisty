use std::sync::{Arc, Mutex};

use async_trait::async_trait;
use chrono::NaiveDateTime;

use shared::domain::types::id::{FleetId, FleetMemberId, UserId};
use shared::infra::database::error::DatabaseError;

use crate::domain::model::FleetMember;
use crate::domain::port::member_repository::MemberRepository;

#[derive(Clone)]
pub struct InMemoryMemberRepository {
    members: Arc<Mutex<Vec<FleetMember>>>,
}

impl InMemoryMemberRepository {
    pub fn new() -> Self {
        InMemoryMemberRepository {
            members: Arc::new(Mutex::new(Vec::new())),
        }
    }
}

#[async_trait]
impl MemberRepository for InMemoryMemberRepository {
    async fn insert(&self, fleet_id: FleetId, user_id: UserId) -> Result<FleetMemberId, DatabaseError> {
        let mut members = self.members.lock().unwrap();

        let fleet_member_id = FleetMemberId::default();
        let member = FleetMember {
            fleet_member_id: fleet_member_id.clone(),
            fleet_id: fleet_id.clone(),
            user_id: user_id.clone(),
            created_at: NaiveDateTime::default(),
            updated_at: NaiveDateTime::default(),
        };

        members.push(member.clone());
        Ok(fleet_member_id.clone())
    }
}
