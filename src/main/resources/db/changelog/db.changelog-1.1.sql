--liquibase formatted sql

--changeset chuckcha:added-admin-account
INSERT INTO users (username, password, role, created_at, modified_at)
VALUES ('admin', '{bcrypt}$2a$12$h9N585Udl9yzdFn/pS5TueNzn7.Xc8ab5eI.KzcGlOk5KJAzALNQO', 'ADMIN', NOW(), NOW());