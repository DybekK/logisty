use async_trait::async_trait;
use chrono::NaiveDateTime;
use std::sync::{Arc, Mutex};

use crate::domain::model::Fleet;
use crate::domain::port::fleet_repository::FleetRepository;

use shared::domain::types::id::FleetId;
use shared::infra::database::error::DatabaseError;

#[derive(Clone)]
pub struct InMemoryFleetRepository {
    fleets: Arc<Mutex<Vec<Fleet>>>,
}

impl InMemoryFleetRepository {
    pub fn new() -> Self {
        InMemoryFleetRepository {
            fleets: Arc::new(Mutex::new(Vec::new())),
        }
    }
}

#[async_trait]
impl FleetRepository for InMemoryFleetRepository {
    async fn find_by_id(&self, id: FleetId) -> Result<Option<Fleet>, DatabaseError> {
        let fleets = self.fleets.lock().unwrap();
        let fleet = fleets.iter().find(|fleet| fleet.fleet_id == id).cloned();

        Ok(fleet)
    }

    async fn find_by_name(&self, fleet_name: String) -> Result<Option<Fleet>, DatabaseError> {
        let fleets = self.fleets.lock().unwrap();
        let fleet = fleets.iter().find(|fleet| fleet.fleet_name == fleet_name).cloned();

        Ok(fleet)
    }

    async fn insert(&self, fleet_name: String) -> Result<FleetId, DatabaseError> {
        let mut fleets = self.fleets.lock().unwrap();

        let fleet_id = FleetId::default();
        let fleet = Fleet {
            fleet_name,
            fleet_id: fleet_id.clone(),
            created_at: NaiveDateTime::default(),
            updated_at: NaiveDateTime::default(),
        };

        fleets.push(fleet.clone());
        Ok(fleet_id)
    }
}
