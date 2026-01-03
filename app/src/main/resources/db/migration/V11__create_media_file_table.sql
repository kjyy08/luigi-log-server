-- Media File 테이블 생성
CREATE TABLE media_file
(
    id                 UUID PRIMARY KEY,
    original_file_name VARCHAR(255)  NOT NULL,
    mime_type          VARCHAR(50)   NOT NULL,
    file_size          BIGINT        NOT NULL,
    storage_key        VARCHAR(500)  NOT NULL UNIQUE,
    public_url         VARCHAR(1000) NOT NULL,
    created_at         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_media_file_storage_key ON media_file (storage_key);
CREATE INDEX idx_media_file_created_at ON media_file (created_at);

-- 테이블 코멘트
COMMENT ON TABLE media_file IS '미디어 파일 메타데이터';
COMMENT ON COLUMN media_file.id IS '파일 ID (UUID)';
COMMENT ON COLUMN media_file.original_file_name IS '원본 파일명';
COMMENT ON COLUMN media_file.mime_type IS 'MIME 타입';
COMMENT ON COLUMN media_file.file_size IS '파일 크기 (bytes)';
COMMENT ON COLUMN media_file.storage_key IS '저장소 키 (Cloudflare R2)';
COMMENT ON COLUMN media_file.public_url IS 'Public URL';
COMMENT ON COLUMN media_file.created_at IS '생성 시간';
COMMENT ON COLUMN media_file.updated_at IS '수정 시간';
