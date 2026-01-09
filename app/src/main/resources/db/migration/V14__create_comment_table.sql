-- 댓글 테이블 생성
CREATE TABLE comment (
    comment_id UUID PRIMARY KEY,
    post_id UUID NOT NULL,
    author_id UUID NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES member(id) ON DELETE CASCADE
);

-- 성능 향상을 위한 인덱스
CREATE INDEX idx_comment_post_id ON comment(post_id);
CREATE INDEX idx_comment_author_id ON comment(author_id);
CREATE INDEX idx_comment_created_at ON comment(created_at DESC);
