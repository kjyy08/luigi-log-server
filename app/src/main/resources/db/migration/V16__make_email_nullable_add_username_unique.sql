-- 1. 이메일 컬럼을 nullable로 변경
ALTER TABLE member 
  ALTER COLUMN email DROP NOT NULL;

-- 2. 이메일 UNIQUE 제약조건 제거
DROP INDEX IF EXISTS idx_member_email;
ALTER TABLE member DROP CONSTRAINT IF EXISTS member_email_key;

-- 3. 이메일 일반 인덱스 재생성 (검색 성능용)
CREATE INDEX idx_member_email ON member(email);

-- 4. Username UNIQUE 제약조건 추가
ALTER TABLE member ADD CONSTRAINT member_username_unique UNIQUE (username);
