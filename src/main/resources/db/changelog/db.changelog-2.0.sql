--liquibase formatted sql

--changeset chuckcha:metadata-table-added
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

--changeset chuckcha:first_index-path_name-added
CREATE INDEX idx_metadata_path_name ON metadata (path, name);

--changeset chuckcha:second_index-path_name_type-added
CREATE INDEX idx_metadata_path_name_type ON metadata (path, name, type);

--changeset chuckcha:created-root-folder-for-admin
INSERT INTO metadata (path, name, size, type, created_at, modified_at)
VALUES ('', 'user-1-files', null, 'DIRECTORY', NOW(), NOW());



