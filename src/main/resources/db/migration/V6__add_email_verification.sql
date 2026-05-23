ALTER TABLE users ADD COLUMN last_verified_at TIMESTAMP;

CREATE TABLE email_verification_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMP    NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    used        BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_evt_token   ON email_verification_tokens(token);
CREATE INDEX idx_evt_user_id ON email_verification_tokens(user_id);
