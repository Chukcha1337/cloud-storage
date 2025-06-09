--liquibase formatted sql

--changeset chuckcha:added-table-metadata
CREATE TABLE IF NOT EXISTS metadata
(
    id BIGSERIAL PRIMARY KEY,
    path VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    size BIGINT,
    type VARCHAR(32),
    created_at TIMESTAMP,
    modified_at TIMESTAMP
);

--changeset chuckcha:added-index-path_name
CREATE INDEX idx_metadata_path_name ON metadata (path, name);

--changeset chuckcha:added-index-path_name_type
CREATE INDEX idx_metadata_path_name_type ON metadata (path, name, type);

