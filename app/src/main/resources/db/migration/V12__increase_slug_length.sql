-- ==========================================
-- V12: Increase slug column length
-- ==========================================

-- slug 컬럼 길이를 255자에서 500자로 증가
-- 한글 지원 및 긴 제목을 위한 충분한 공간 확보
ALTER TABLE post
ALTER COLUMN slug TYPE VARCHAR(500);
