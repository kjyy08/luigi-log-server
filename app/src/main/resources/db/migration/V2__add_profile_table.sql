-- ==========================================
-- Profile Table (1:1 with Member)
-- ==========================================
CREATE TABLE profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    member_id UUID NOT NULL UNIQUE REFERENCES member(id) ON DELETE CASCADE,
    nickname VARCHAR(100) NOT NULL,
    bio VARCHAR(500),
    profile_image_url VARCHAR(2000),
    job_title VARCHAR(100),
    tech_stack TEXT,
    github_url VARCHAR(500),
    contact_email VARCHAR(255),
    website_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_profile_member_id ON profile(member_id);
CREATE INDEX idx_profile_nickname ON profile(nickname);

-- Default profiles for existing members
INSERT INTO profile (member_id, nickname)
SELECT id, username FROM member;
