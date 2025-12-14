-- 1. Create MemberCredentials Table
CREATE TABLE member_credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id UUID NOT NULL UNIQUE,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    last_login_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_credentials_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT uk_member_credentials_provider_id UNIQUE (provider, provider_id)
);

CREATE INDEX idx_member_credentials_member_id ON member_credentials(member_id);
CREATE INDEX idx_member_credentials_provider ON member_credentials(provider, provider_id);

-- 2. Migrate Data
INSERT INTO member_credentials (member_id, provider, provider_id, created_at, updated_at)
SELECT id, provider, provider_id, created_at, updated_at
FROM member;

-- 3. Alter Member Table (Drop columns)
ALTER TABLE member DROP COLUMN provider;
ALTER TABLE member DROP COLUMN provider_id;
