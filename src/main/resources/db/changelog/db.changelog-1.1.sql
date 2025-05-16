--liquibase formatted sql

--changeset chuckcha:added admin account
INSERT INTO users (username, password, role, created_at, modified_at)
VALUES ('admin', '{bcrypt}$2a$10$dKyHLwp8l8pFzTwOxDK4V.CSVE34OmALdrGbRjykN0jJWv86Sqi5m', 'ADMIN', NOW(), NOW());