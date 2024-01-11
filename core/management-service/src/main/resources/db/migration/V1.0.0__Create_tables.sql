CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    name     VARCHAR(100),
    email    VARCHAR(100),
    password VARCHAR(100)
);

CREATE TABLE organizations
(
    id   UUID PRIMARY KEY,
    name VARCHAR(100)
);

CREATE TABLE organization_memberships
(
    membership_id   UUID PRIMARY KEY,
    user_id         UUID REFERENCES users (id),
    organization_id UUID REFERENCES organizations (id),
    role            VARCHAR(100)
);
