CREATE TABLE users
(
    user_id    CUID                     PRIMARY KEY,
    email      VARCHAR(100)             NOT NULL,
    password   VARCHAR(100)             NOT NULL,
    role       VARCHAR(20)              NOT NULL,
    created_at TIMESTAMP                NOT NULL,
    updated_at TIMESTAMP                NOT NULL
);

CREATE TABLE invitations
(
    invitation_id CUID                     PRIMARY KEY,
    first_name    VARCHAR(100)             NOT NULL,
    last_name     VARCHAR(100)             NOT NULL,
    email         VARCHAR(100)             NOT NULL,
    fleet_id      CUID,                    -- This will be referenced from the fleets microservice
    role          VARCHAR(20)              NOT NULL,
    status        VARCHAR(20)              NOT NULL,
    created_at    TIMESTAMP                NOT NULL,
    due_at        TIMESTAMP                NOT NULL,
    accepted_at   TIMESTAMP,
    denied_at     TIMESTAMP
);

CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_email_inv ON invitations (email);
