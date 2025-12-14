ALTER TABLE profile DROP CONSTRAINT IF EXISTS profile_member_id_fkey;
DROP INDEX IF EXISTS idx_profile_member_id;
ALTER TABLE profile DROP COLUMN member_id;

ALTER TABLE member ADD COLUMN profile_id UUID UNIQUE;
ALTER TABLE member ADD CONSTRAINT member_profile_id_fkey
    FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE;

CREATE INDEX idx_member_profile_id ON member(profile_id);
