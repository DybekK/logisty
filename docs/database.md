
### Users

**Database: `users`**
```sql
CREATE TABLE users
  (
    user_id       CUID         PRIMARY KEY,
    username      VARCHAR(255) UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    status_id     INT          REFERENCES user_status (status_id) DEFAULT 1
  );

CREATE TABLE user_status
  (
    status_id   CUID        PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
  );

CREATE TABLE roles
  (
    role_id   CUID        PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
  );

CREATE TABLE user_roles
  (
    user_role_id CUID      PRIMARY KEY,
    user_id      CUID      REFERENCES users (user_id),
    role_id      CUID      REFERENCES roles (role_id),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE invitations
  (
    invitation_id CUID         PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    fleet_id      CUID,        -- This will be referenced from the fleets microservice
    role_id       CUID         REFERENCES roles (role_id),
    status_id     CUID         REFERENCES user_status (status_id) DEFAULT 1,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    accepted_at   TIMESTAMP
  );

CREATE INDEX idx_user_id ON user_roles (user_id);
CREATE INDEX idx_role_id ON user_roles (role_id);
CREATE INDEX idx_email_inv ON invitations (email);
CREATE INDEX idx_role_id_inv ON invitations (role_id);
CREATE INDEX idx_status_id_inv ON invitations (status_id); 
```

### Fleets

**Database: `fleets`**

```sql
CREATE TABLE fleets
  (
    fleet_id   CUID         PRIMARY KEY,
    fleet_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE fleet_members
  (
    fleet_member_id CUID      PRIMARY KEY,
    fleet_id        CUID      REFERENCES fleets (fleet_id),
    user_id         CUID,     -- This will be referenced from the users microservice
    role_id         CUID,     -- This will be referenced from the users microservice
    status_id       CUID,     -- This will be referenced from the users microservice
    email           VARCHAR(255),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE cars
  (
    car_id            CUID         PRIMARY KEY,
    fleet_id          CUID         REFERENCES fleets (fleet_id),
    car_model         VARCHAR(255) NOT NULL,
    car_license_plate VARCHAR(50)  NOT NULL UNIQUE,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE driver_fleets
  (
    driver_fleet_id CUID      PRIMARY KEY,
    driver_id       CUID,     -- This will be referenced from the drivers microservice
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
```

### Drivers

**Database: `drivers`**

```sql
CREATE TABLE drivers
  (
    driver_id      CUID         PRIMARY KEY,
    driver_name    VARCHAR(255) NOT NULL,
    license_number VARCHAR(50)  NOT NULL UNIQUE,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
  );
  
CREATE TABLE driver_responses
  (
    driver_response_id     CUID        PRIMARY KEY,
    order_id               CUID,       -- This will be referenced from the orders microservice
    driver_id              CUID        REFERENCES drivers (driver_id),
    status                 VARCHAR(50) NOT NULL,  -- 'accepted' or 'rejected'
    updated_at             TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
  );
  
CREATE TABLE driver_checkins
  (
    checkin_id     CUID        PRIMARY KEY,
    driver_id      CUID        REFERENCES drivers (driver_id),
    location_id    CUID,       -- This will be referenced from the orders microservice
    checkin_status VARCHAR(50) NOT NULL,  -- 'checked_in', 'failed'
    checkin_time   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
  );

CREATE INDEX idx_driver_id ON drivers(driver_id);
CREATE INDEX idx_order_id_order_responses ON order_responses(order_id);
CREATE INDEX idx_driver_id_order_responses ON order_responses(driver_id);
CREATE INDEX idx_driver_id_checkins ON driver_checkins(driver_id);
CREATE INDEX idx_location_id_checkins ON driver_checkins(location_id);
```

### Orders

**Database: `orders`**

```sql
CREATE TABLE locations
  (
    location_id   CUID             PRIMARY KEY,
    location_name VARCHAR(255)     NOT NULL,
    latitude      DOUBLE PRECISION NOT NULL,
    longitude     DOUBLE PRECISION NOT NULL
  );

CREATE TABLE orders
  (
    order_id          CUID        PRIMARY KEY,
    user_id           CUID,       -- This will be referenced from the users microservice
    fleet_id          CUID,       -- This will be referenced from the fleets microservice
    driver_id         CUID,       -- This will be referenced from the drivers microservice
    car_id            CUID,       -- This will be referenced from the fleets microservice
    order_status      VARCHAR(50) NOT NULL,
    order_details     TEXT,
    start_location_id CUID        REFERENCES locations (location_id),  -- Reference to locations table
    end_location_id   CUID        REFERENCES locations (location_id),    -- Reference to locations table
    cached_path       JSONB,
    created_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
  );

CREATE TABLE order_steps
  (
    order_step_id     CUID      PRIMARY KEY,
    order_id          CUID      REFERENCES orders (order_id) ON DELETE CASCADE,
    step_number       CUID      NOT NULL,
    start_location_id CUID      REFERENCES locations (location_id),  -- Reference to locations table
    end_location_id   CUID      REFERENCES locations (location_id),    -- Reference to locations table
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );

CREATE INDEX idx_user_id_orders ON orders (user_id);
CREATE INDEX idx_fleet_id_orders ON orders (fleet_id);
CREATE INDEX idx_driver_id_orders ON orders (driver_id);
CREATE INDEX idx_car_id_orders ON orders (car_id);
CREATE INDEX idx_start_location_id ON orders (start_location_id);
CREATE INDEX idx_end_location_id ON orders (end_location_id);
CREATE INDEX idx_order_id_steps ON order_steps (order_id);
CREATE INDEX idx_start_location_id_steps ON order_steps (start_location_id);
CREATE INDEX idx_end_location_id_steps ON order_steps (end_location_id);
```