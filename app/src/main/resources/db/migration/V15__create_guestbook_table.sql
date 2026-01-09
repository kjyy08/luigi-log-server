CREATE TABLE guestbook (
    guestbook_id UUID PRIMARY KEY,
    author_id UUID NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_guestbook_author_id ON guestbook(author_id);
CREATE INDEX idx_guestbook_created_at ON guestbook(created_at DESC);
