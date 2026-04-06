CREATE TABLE children (
    id BIGSERIAL PRIMARY KEY,
    parent_user_id BIGINT NOT NULL REFERENCES users(id),
    child_user_id BIGINT REFERENCES users(id),
    name VARCHAR(20) NOT NULL,
    avatar VARCHAR(255) NOT NULL DEFAULT '🐣',
    theme_color VARCHAR(7) NOT NULL DEFAULT '#0060ad',
    birth_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
