--liquibase formatted sql

--changeset chuckcha:added users table
CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP,
    modified_at TIMESTAMP,
    role VARCHAR(32)
);