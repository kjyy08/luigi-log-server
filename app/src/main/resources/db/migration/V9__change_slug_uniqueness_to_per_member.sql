-- ==========================================
-- V9: Change slug uniqueness to per-member
-- ==========================================

-- (member_id, slug) 복합 UNIQUE 제약 추가
ALTER TABLE post
ADD CONSTRAINT uk_post_member_slug UNIQUE (member_id, slug);
