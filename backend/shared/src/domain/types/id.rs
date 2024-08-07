use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct UserId(pub String);

impl Default for UserId {
    fn default() -> Self {
        UserId(cuid::cuid2())
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct FleetId(pub String);

impl Default for FleetId {
    fn default() -> Self {
        FleetId(cuid::cuid2())
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct FleetMemberId(pub String);

impl Default for FleetMemberId {
    fn default() -> Self {
        FleetMemberId(cuid::cuid2())
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct CarId(pub String);

impl Default for CarId {
    fn default() -> Self {
        CarId(cuid::cuid2())
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct DriverFleetId(pub String);

impl Default for DriverFleetId {
    fn default() -> Self {
        DriverFleetId(cuid::cuid2())
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct RoleId(pub String);

impl Default for RoleId {
    fn default() -> Self {
        RoleId(cuid::cuid2())
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct StatusId(pub String);

impl Default for StatusId {
    fn default() -> Self {
        StatusId(cuid::cuid2())
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Deserialize, Serialize, sqlx::Type)]
#[sqlx(transparent)]
pub struct DriverId(pub String);

impl Default for DriverId {
    fn default() -> Self {
        DriverId(cuid::cuid2())
    }
}
