-- Outbox 메시지 테이블
CREATE TABLE IF NOT EXISTS outbox_messages (
    id UUID PRIMARY KEY,
    payload BYTEA NOT NULL,
    topic VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    last_attempt_time TIMESTAMP,
    error_message TEXT,
    version BIGINT NOT NULL DEFAULT 0
);

-- Outbox 메시지 헤더 테이블
CREATE TABLE IF NOT EXISTS outbox_message_headers (
    outbox_message_id UUID NOT NULL,
    header_key VARCHAR(255) NOT NULL,
    header_value TEXT NOT NULL,
    CONSTRAINT pk_outbox_message_headers PRIMARY KEY (outbox_message_id, header_key),
    CONSTRAINT fk_outbox_message_headers_message_id FOREIGN KEY (outbox_message_id)
        REFERENCES outbox_messages (id) ON DELETE CASCADE
);

-- 인덱스
CREATE INDEX IF NOT EXISTS idx_outbox_messages_status ON outbox_messages (status);
CREATE INDEX IF NOT EXISTS idx_outbox_messages_created_at ON outbox_messages (created_at);
CREATE INDEX IF NOT EXISTS idx_outbox_messages_retry_count ON outbox_messages (retry_count);
CREATE INDEX IF NOT EXISTS idx_outbox_messages_last_attempt_time ON outbox_messages (last_attempt_time); 