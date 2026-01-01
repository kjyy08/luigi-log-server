-- ==========================================
-- 1. Member Context
-- ==========================================
CREATE TABLE member (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT member_provider_unique UNIQUE (provider, provider_id)
);

CREATE INDEX idx_member_email ON member(email);
CREATE INDEX idx_member_provider ON member(provider, provider_id);

-- ==========================================
-- 2. Content Context
-- ==========================================
CREATE TYPE content_type AS ENUM ('BLOG', 'PORTFOLIO');
CREATE TYPE post_status AS ENUM ('DRAFT', 'PUBLISHED', 'ARCHIVED');

CREATE TABLE post (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(500) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    body TEXT NOT NULL,
    type content_type NOT NULL,
    status post_status NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_post_slug ON post(slug);
CREATE INDEX idx_post_type_status ON post(type, status);
CREATE INDEX idx_post_created_at ON post(created_at DESC);

-- Tag Table
CREATE TABLE tag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE INDEX idx_tag_name ON tag(name);

-- Post-Tag Many-to-Many
CREATE TABLE post_tag (
    post_id UUID NOT NULL REFERENCES post(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tag(id) ON DELETE CASCADE,
    PRIMARY KEY (post_id, tag_id)
);

CREATE INDEX idx_post_tag_post_id ON post_tag(post_id);
CREATE INDEX idx_post_tag_tag_id ON post_tag(tag_id);

