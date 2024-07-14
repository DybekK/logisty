CREATE TABLE users
(
    id         VARCHAR PRIMARY KEY,
    email      VARCHAR                  NOT NULL,
    password   VARCHAR                  NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
