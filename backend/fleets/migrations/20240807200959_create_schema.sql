CREATE TABLE fleets
  (
    fleet_id   CUID         PRIMARY KEY,
    fleet_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE fleet_members
  (
    fleet_member_id CUID      PRIMARY KEY,
    fleet_id        CUID      REFERENCES fleets (fleet_id),
    user_id         CUID,     -- This is referenced from the users microservice
    role_id         CUID,     -- This is referenced from the users microservice
    status_id       CUID,     -- This is referenced from the users microservice
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE cars
  (
    car_id            CUID         PRIMARY KEY,
    fleet_id          CUID         REFERENCES fleets (fleet_id),
    car_model         VARCHAR(255) NOT NULL,
    car_license_plate VARCHAR(50)  NOT NULL UNIQUE,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
    updated_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE driver_fleets
  (
    driver_fleet_id CUID      PRIMARY KEY,
    driver_id       CUID,     -- This is referenced from the drivers microservice
    fleet_id        CUID      REFERENCES fleets (fleet_id),
    car_id          CUID      REFERENCES cars (car_id),
    assigned_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );

CREATE INDEX idx_fleet_id ON fleet_members (fleet_id);
CREATE INDEX idx_user_id_fm ON fleet_members (user_id);
CREATE INDEX idx_role_id_fm ON fleet_members (role_id);
CREATE INDEX idx_car_id ON cars (car_id);
CREATE INDEX idx_fleet_id_car ON cars (fleet_id);
CREATE INDEX idx_driver_id ON driver_fleets (driver_id);
CREATE INDEX idx_fleet_id_df ON driver_fleets (fleet_id);
CREATE INDEX idx_car_id_df ON driver_fleets (car_id);
