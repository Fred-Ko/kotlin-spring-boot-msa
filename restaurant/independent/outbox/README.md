# Outbox Module

이 모듈은 도메인 이벤트를 안정적으로 발행하기 위한 Outbox 패턴을 구현합니다.

## 개요

Outbox 패턴은 분산 시스템에서 데이터 일관성을 보장하기 위한 패턴입니다. 도메인 이벤트를 즉시 메시지 브로커로 발행하는 대신,
먼저 데이터베이스의 outbox 테이블에 저장한 후 별도의 프로세스가 이를 메시지 브로커로 발행합니다.

## 주요 기능

- 도메인 이벤트의 안정적인 발행
- 메시지 재시도 및 실패 처리
- Dead Letter Queue (DLQ) 지원
- 동시성 제어를 통한 메시지 순서 보장
- 멱등성 보장

## 사용법

### 1. 의존성 추가

```kotlin
// build.gradle.kts
implementation(project(":independent:outbox:application"))
```

### 2. 설정 추가

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      acks: all
      retries: 3
      batch-size: 16384
      buffer-memory: 33554432
      compression-type: lz4
      properties:
        delivery.timeout.ms: 120000
        request.timeout.ms: 30000
        max.block.ms: 60000

outbox:
  polling:
    batch-size: 100
    max-retries: 3
    pending-messages-interval: 5000
    failed-messages-interval: 60000
```

### 3. 도메인 이벤트 구현

```kotlin
class UserCreatedEvent(
    private val userId: UUID,
    private val username: String
) : OutboxDomainEvent {
    override fun getTopic(): String = "user-events"

    override fun getHeaders(): Map<String, String> = mapOf(
        "aggregateType" to "user",
        "aggregateId" to userId.toString(),
        "eventType" to "UserCreated"
    )

    override fun serialize(): ByteArray {
        // Avro 또는 다른 형식으로 직렬화
    }
}
```

### 4. 이벤트 발행

```kotlin
@Service
class UserService(
    private val outboxService: OutboxService
) {
    @Transactional
    fun createUser(username: String) {
        // 사용자 생성 로직
        val user = User(username = username)
        userRepository.save(user)

        // 이벤트 발행
        val event = UserCreatedEvent(user.id, user.username)
        outboxService.publish(event)
    }
}
```

## 아키텍처

```
+----------------+     +----------------+     +----------------+
|                |     |                |     |                |
| Domain Service |---->| Outbox Service |---->| Database      |
|                |     |                |     |                |
+----------------+     +----------------+     +----------------+
                                                    |
                                                    |
                                                    v
                                            +----------------+
                                            |                |
                                            | Outbox Poller  |
                                            |                |
                                            +----------------+
                                                    |
                                                    |
                                                    v
                                            +----------------+
                                            |                |
                                            | Kafka          |
                                            |                |
                                            +----------------+
```

## 데이터베이스 스키마

```sql
CREATE TABLE outbox_messages (
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

CREATE TABLE outbox_message_headers (
    outbox_message_id UUID NOT NULL,
    header_key VARCHAR(255) NOT NULL,
    header_value TEXT NOT NULL,
    CONSTRAINT pk_outbox_message_headers PRIMARY KEY (outbox_message_id, header_key),
    CONSTRAINT fk_outbox_message_headers_message_id FOREIGN KEY (outbox_message_id)
        REFERENCES outbox_messages (id) ON DELETE CASCADE
);
```

## 에러 처리

모든 에러는 `OutboxException` 클래스를 통해 처리됩니다:

- `MessageSaveFailed`: 메시지 저장 실패
- `MessageSerializationFailed`: 메시지 직렬화 실패
- `MessageSendFailed`: 메시지 전송 실패
- `MessageProcessingFailed`: 메시지 처리 실패
- `MaxRetriesExceeded`: 최대 재시도 횟수 초과
- `SystemError`: 시스템 에러

## 모니터링

다음 지표들을 모니터링하는 것을 권장합니다:

- 대기 중인 메시지 수
- 실패한 메시지 수
- Dead Letter Queue에 있는 메시지 수
- 메시지 처리 시간
- 재시도 횟수 분포

## 주의사항

1. 트랜잭션 관리
   - 도메인 로직과 이벤트 저장이 같은 트랜잭션에서 실행되어야 합니다.
   - `@Transactional` 어노테이션을 적절히 사용하세요.

2. 성능
   - 배치 크기와 폴링 간격을 적절히 조정하세요.
   - 메시지 크기가 너무 크지 않도록 주의하세요.

3. 모니터링
   - 실패한 메시지와 Dead Letter Queue를 주기적으로 모니터링하세요.
   - 적절한 알림을 설정하세요.

4. 보안
   - 민감한 정보는 암호화하여 저장하세요.
   - 적절한 접근 제어를 설정하세요.
