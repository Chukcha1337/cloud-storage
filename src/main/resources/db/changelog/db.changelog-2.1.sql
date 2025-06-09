--liquibase formatted sql

--changeset chuckcha:created-index-path_name
CREATE INDEX IF NOT EXISTS idx_metadata_path_name ON metadata (path, name);


