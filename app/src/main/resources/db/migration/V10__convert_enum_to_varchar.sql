-- ==========================================
-- V10: Convert PostgreSQL ENUM to VARCHAR
-- ==========================================
-- PostgreSQL ENUM 타입을 VARCHAR로 변환하여 JPA @Enumerated(EnumType.STRING)과 호환성 확보
-- ENUM 타입의 제약 (값 추가/변경 어려움)을 해결하고 DB 이식성 향상

-- 1. DEFAULT 값 제거 (ENUM 타입 의존성 제거)
ALTER TABLE post
  ALTER COLUMN status DROP DEFAULT;

-- 2. 컬럼 타입을 VARCHAR로 변경 (USING 절로 명시적 캐스팅)
ALTER TABLE post
  ALTER COLUMN type TYPE VARCHAR(50) USING type::text,
  ALTER COLUMN status TYPE VARCHAR(50) USING status::text;

-- 3. DEFAULT 값 재설정 (VARCHAR로)
ALTER TABLE post
  ALTER COLUMN status SET DEFAULT 'DRAFT';

-- 4. ENUM 타입 삭제
DROP TYPE IF EXISTS content_type;
DROP TYPE IF EXISTS post_status;

-- 5. CHECK 제약 조건 추가 (ENUM과 동일한 제약)
ALTER TABLE post
  ADD CONSTRAINT check_content_type
  CHECK (type IN ('BLOG', 'PORTFOLIO'));

ALTER TABLE post
  ADD CONSTRAINT check_post_status
  CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'));
