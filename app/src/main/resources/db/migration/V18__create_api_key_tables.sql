CREATE TABLE api_key (
    id UUID PRIMARY KEY,
    owner_member_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    prefix VARCHAR(32) NOT NULL,
    key_hash VARCHAR(64) NOT NULL UNIQUE,
    scopes VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP,
    last_used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_api_key_owner_member FOREIGN KEY (owner_member_id) REFERENCES member (id)
);

CREATE INDEX idx_api_key_owner_member_id ON api_key (owner_member_id);
CREATE INDEX idx_api_key_prefix ON api_key (prefix);

CREATE TABLE api_key_audit_log (
    id UUID PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    api_key_id UUID,
    prefix VARCHAR(32),
    path VARCHAR(255),
    result VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_api_key_audit_log_api_key FOREIGN KEY (api_key_id) REFERENCES api_key (id)
);

CREATE INDEX idx_api_key_audit_log_api_key_id ON api_key_audit_log (api_key_id);
CREATE INDEX idx_api_key_audit_log_created_at ON api_key_audit_log (created_at);
