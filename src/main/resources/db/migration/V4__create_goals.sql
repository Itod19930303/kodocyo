CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    child_id BIGINT NOT NULL REFERENCES children(id) ON DELETE CASCADE,
    name VARCHAR(30) NOT NULL,
    target_amount INTEGER NOT NULL CHECK (target_amount > 0),
    target_date DATE,
    purpose_category VARCHAR(20),
    message VARCHAR(200),
    emoji VARCHAR(10) NOT NULL DEFAULT '🎯',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
