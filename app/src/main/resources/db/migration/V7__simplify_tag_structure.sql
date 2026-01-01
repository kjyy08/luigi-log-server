-- ==========================================
-- V7: Simplify Tag Structure (@ElementCollection)
-- ==========================================

-- 1. 기존 데이터 임시 테이블로 백업 (데이터 있을 경우)
CREATE TEMP TABLE post_tag_backup AS
SELECT pt.post_id, t.name as tag_name
FROM post_tag pt
INNER JOIN tag t ON pt.tag_id = t.id;

-- 2. 기존 테이블 삭제 (외래키 제약 때문에 순서 중요)
DROP TABLE IF EXISTS post_tag CASCADE;
DROP TABLE IF EXISTS tag CASCADE;

-- 3. 새로운 post_tag 테이블 생성 (@ElementCollection)
CREATE TABLE post_tag (
    post_id UUID NOT NULL REFERENCES post(id) ON DELETE CASCADE,
    tag_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (post_id, tag_name)
);

-- 4. 데이터 복원 (백업이 있을 경우)
INSERT INTO post_tag (post_id, tag_name)
SELECT post_id, tag_name
FROM post_tag_backup
WHERE EXISTS (SELECT 1 FROM post_tag_backup);

-- 5. 인덱스 생성
CREATE INDEX idx_post_tag_post_id ON post_tag(post_id);
CREATE INDEX idx_post_tag_name ON post_tag(tag_name);  -- 태그별 Post 조회용

-- 6. 임시 테이블 정리
DROP TABLE IF EXISTS post_tag_backup;
