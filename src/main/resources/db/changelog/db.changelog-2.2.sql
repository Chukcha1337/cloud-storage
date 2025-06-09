--liquibase formatted sql

--changeset chuckcha:created-index-path_name_type
CREATE INDEX IF NOT EXISTS idx_metadata_path_name_type ON metadata (path, name, type);

