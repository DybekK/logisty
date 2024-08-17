use chrono::NaiveDateTime;
use serde::{Deserialize, Serialize};
use sqlx::FromRow;

use shared::domain::types::id::{CarId, DriverFleetId, DriverId, FleetId, FleetMemberId, UserId};

#[derive(Debug, Clone, Serialize, Deserialize, FromRow)]
pub struct Fleet {
    pub fleet_id: FleetId,
    pub fleet_name: String,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Debug, Clone, Serialize, Deserialize, FromRow)]
pub struct FleetMember {
    pub fleet_member_id: FleetMemberId,
    pub fleet_id: FleetId,
    pub user_id: UserId,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Debug, Clone, Serialize, Deserialize, FromRow)]
pub struct Car {
    pub car_id: CarId,
    pub fleet_id: FleetId,
    pub car_model: String,
    pub car_license_plate: String,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Debug, Clone, Serialize, Deserialize, FromRow)]
pub struct DriverFleet {
    pub driver_fleet_id: DriverFleetId,
    pub driver_id: DriverId,
    pub fleet_id: FleetId,
    pub car_id: CarId,
    pub assigned_at: NaiveDateTime,
}
