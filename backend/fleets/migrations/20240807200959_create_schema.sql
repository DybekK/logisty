CREATE TABLE fleets
(
    fleet_id   CUID         PRIMARY KEY,
    fleet_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE fleet_members
(
    fleet_member_id CUID      PRIMARY KEY,
    fleet_id        CUID      REFERENCES fleets (fleet_id),
    user_id         CUID,     -- This is referenced from the users microservice
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

CREATE TABLE cars
(
    car_id            CUID         PRIMARY KEY,
    fleet_id          CUID         REFERENCES fleets (fleet_id),
    car_model         VARCHAR(255) NOT NULL,
    car_license_plate VARCHAR(50)  NOT NULL UNIQUE,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL
);

CREATE INDEX idx_fleet_id_fm ON fleet_members (fleet_id);
CREATE INDEX idx_user_id_fm ON fleet_members (user_id);
CREATE INDEX idx_car_id ON cars (car_id);
CREATE INDEX idx_fleet_id_car ON cars (fleet_id);