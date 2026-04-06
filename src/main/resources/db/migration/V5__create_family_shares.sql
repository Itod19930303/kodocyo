CREATE TABLE family_shares (
    id BIGSERIAL PRIMARY KEY,
    owner_user_id BIGINT NOT NULL REFERENCES users(id),
    shared_user_id BIGINT REFERENCES users(id),
    invited_email VARCHAR(255) NOT NULL,
    share_role VARCHAR(20) NOT NULL CHECK (share_role IN ('PARTNER','GRANDPARENT')),
    permission VARCHAR(20) NOT NULL CHECK (permission IN ('VIEW_ONLY','EDIT')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','ACCEPTED','REVOKED')),
    invite_token VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
