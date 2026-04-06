CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    child_id BIGINT NOT NULL REFERENCES children(id) ON DELETE CASCADE,
    type VARCHAR(10) NOT NULL CHECK (type IN ('income','expense')),
    amount INTEGER NOT NULL CHECK (amount > 0),
    category VARCHAR(20),
    memo VARCHAR(100),
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
