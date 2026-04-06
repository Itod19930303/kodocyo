CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    oauth_provider VARCHAR(20),
    oauth_subject VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'PARENT',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
