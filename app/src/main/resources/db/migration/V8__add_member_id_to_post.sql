-- ==========================================
-- V8: Add member_id to post table
-- ==========================================

-- 1. member_id 컬럼 추가
ALTER TABLE post
ADD COLUMN member_id UUID;

-- 2. 기존 데이터가 있을 경우를 위한 임시 값 설정 (있다면 첫 번째 member로 설정)
-- 실제 프로덕션에서는 데이터 마이그레이션 로직 필요
UPDATE post
SET member_id = (SELECT id FROM member LIMIT 1)
WHERE member_id IS NULL;

-- 3. NOT NULL 제약 추가
ALTER TABLE post
ALTER COLUMN member_id SET NOT NULL;

-- 4. 외래키 제약 추가
ALTER TABLE post
ADD CONSTRAINT fk_post_member
FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE;

-- 5. 인덱스 생성 (member별 post 조회 최적화)
CREATE INDEX idx_post_member_id ON post(member_id);
