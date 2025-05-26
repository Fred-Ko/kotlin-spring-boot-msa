-- Outbox Events 테이블 생성 및 최적화
-- Rule 83: Outbox 모듈의 데이터베이스 스키마 정의
-- Rule 87: 동시성 제어를 위한 인덱스 및 최적화
-- Rule 86: Outbox 폴링 성능 최적화를 위한 인덱스

CREATE TABLE outbox_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload BLOB NOT NULL,
    headers TEXT NOT NULL,
    event_type VARCHAR(255) NOT NULL DEFAULT 'DEFAULT_EVENT_TYPE',
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    last_attempt_time TIMESTAMP,
    retry_count INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT outbox_events_status_check CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'DEAD_LETTERED', 'DISCARDED'))
);

-- Rule 87: 동시성 제어를 위한 인덱스 및 최적화
-- Rule 86: Outbox 폴링 성능 최적화를 위한 인덱스

-- 상태별 조회 성능 향상을 위한 인덱스
CREATE INDEX idx_outbox_events_status_created_at ON outbox_events (status, created_at);

-- 재시도 대상 메시지 조회를 위한 인덱스
CREATE INDEX idx_outbox_events_status_retry_count ON outbox_events (status, retry_count);

-- 폴링 시 SKIP LOCKED를 위한 복합 인덱스 (H2 호환성을 위해 WHERE 절 제거)
CREATE INDEX idx_outbox_events_pending_processing ON outbox_events (status, last_attempt_time, created_at);

-- Aggregate 기반 조회를 위한 인덱스
CREATE INDEX idx_outbox_events_aggregate ON outbox_events (aggregate_type, aggregate_id);

-- 정리 작업을 위한 인덱스 (성공한 메시지 정리용, H2 호환성을 위해 WHERE 절 제거)
CREATE INDEX idx_outbox_events_sent_created_at ON outbox_events (status, created_at); 