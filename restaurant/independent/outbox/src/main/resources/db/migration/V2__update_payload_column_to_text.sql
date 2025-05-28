-- Outbox Events 테이블의 payload 컬럼 타입을 BLOB에서 TEXT로 변경
-- Rule 83: Outbox 모듈의 데이터베이스 스키마 정의 업데이트

-- payload 컬럼 타입 변경
ALTER TABLE outbox_events MODIFY COLUMN payload TEXT NOT NULL;
