# Outbox Module

이 모듈은 도메인 이벤트를 안정적으로 발행하기 위한 Outbox 패턴을 구현하는 **완전 독립적인 모듈**입니다.

## 개요

**Rule 80**: 이 모듈은 `independent/` 폴더 아래에 위치하는 독립 모듈로, 프로젝트 내의 어떤 특정 도메인 모듈이나 `domains/common` 모듈에도 의존하지 않아 완전한 독립성과 이식성을 보장합니다.

Outbox 패턴은 분산 시스템에서 데이터 일관성을 보장하기 위한 패턴입니다. 도메인 이벤트를 즉시 메시지 브로커로 발행하는 대신, 먼저 데이터베이스의 outbox 테이블에 저장한 후 별도의 프로세스가 이를 메시지 브로커로 발행합니다.

## 주요 기능

- **Rule 78**: 트랜잭셔널 아웃박스 패턴을 통한 애플리케이션 상태 변경과 이벤트 발행의 원자성 보장
- **Rule 87**: 동시성 제어를 위한 데이터베이스 수준 잠금 (FOR UPDATE SKIP LOCKED) 사용
- **Rule 90**: 지수 백오프를 포함한 재시도 및 실패 처리
- **Rule VII.1**: Avro4k 방법론을 지원하는 ByteArray 페이로드 처리
- **Rule VII.2.23**: 메시지 상태별 모니터링 및 메트릭 수집
- Dead Letter Queue (DLQ) 지원
- 멱등성 보장

## 아키텍처

**Rule 80**: 독립 모듈의 자체적인 레이어 구조

```
independent/outbox/
├── application/                    # Application 레이어
│   ├── usecase/                   # Use Case 인터페이스
│   │   └── ProcessOutboxEventsUseCase.kt
│   ├── handler/                   # Use Case 구현체 및 폴링 컴포넌트
│   │   ├── ProcessOutboxEventsUseCaseHandler.kt
│   │   ├── OutboxPoller.kt       # Rule 86: 폴링/전송 컴포넌트
│   │   └── OutboxMetricsHandler.kt # Rule VII.2.23: 모니터링
│   └── dto/                       # DTO 및 Repository 인터페이스
│       ├── OutboxMessageRepository.kt # Rule 81
│       └── model/
│           ├── OutboxMessage.kt   # Rule 81: 메시지 구조 정의
│           └── OutboxMessageStatus.kt
└── infrastructure/                # Infrastructure 레이어
    ├── entity/                    # JPA Entity
    │   └── OutboxEventEntity.kt   # Rule 83
    ├── repository/                # Repository 구현체
    │   ├── JpaOutboxEventRepository.kt
    │   ├── JpaOutboxMessageRepository.kt
    │   └── OutboxMessageRepositoryImpl.kt # Rule 82
    ├── messaging/                 # 메시지 브로커 컴포넌트
    │   ├── OutboxMessageSender.kt # Rule 88
    │   └── config/
    │       └── KafkaOutboxProducerConfig.kt # Rule VII.1, VII.2.17
    ├── converter/                 # JPA Converter
    │   └── StringMapConverter.kt  # Rule 83
    ├── error/                     # 에러 코드
    │   └── OutboxErrorCodes.kt    # Rule 67, 80, 90
    └── exception/                 # 예외
        └── OutboxException.kt     # Rule 68, 80, 90
```

## 사용법

### 1. 의존성 추가

```kotlin
// build.gradle.kts
implementation(project(":independent:outbox"))
```

**Rule 80**: 다른 모듈들은 이 독립 모듈의 **Application Layer Use Case 인터페이스**만을 의존합니다.

### 2. 설정 추가

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    schema-registry-url: http://localhost:8081  # Rule VII.1: Avro4k 지원
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.ByteArraySerializer
      acks: all
      retries: 3
      enable-idempotence: true

outbox:
  polling:
    batch-size: 100
    max-retries: 3
    pending-messages-interval: 1000
    failed-messages-interval: 300000
```

### 3. 도메인 이벤트 구현

**Rule VII.1**: 코틀린 Avro4k 방법론을 사용한 DomainEvent 정의

```kotlin
@Serializable  // Rule VII.1: kotlinx.serialization 사용
data class UserCreatedEvent(
    val userId: String,
    val username: String,
    val timestamp: Instant = Instant.now()
) : DomainEvent {
    override fun getAggregateId(): String = userId
    override fun getEventType(): String = "UserCreated"
}
```

### 4. 이벤트 발행

**Rule 85**: Repository 구현체에서 이벤트 처리 및 메시지 변환

```kotlin
@Service
class UserRepositoryImpl(
    private val outboxMessageRepository: OutboxMessageRepository,
    private val domainEventToOutboxMessageConverter: DomainEventToOutboxMessageConverter
) : UserRepository {
    
    @Transactional
    override fun save(user: User): User {
        // 사용자 저장 로직
        val savedUser = jpaUserRepository.save(user.toEntity()).toDomain()
        
        // Rule 85: 도메인 이벤트를 Outbox 메시지로 변환하여 저장
        val domainEvents = user.getDomainEvents()
        val outboxMessages = domainEventToOutboxMessageConverter.convert(domainEvents)
        outboxMessageRepository.saveAll(outboxMessages)
        
        user.clearDomainEvents()
        return savedUser
    }
}
```

## 데이터베이스 스키마

**Rule 83**: Outbox 이벤트 엔티티 구조

```sql
CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload BYTEA NOT NULL,              -- Rule VII.1: Avro 바이너리 저장
    headers TEXT NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    last_attempt_time TIMESTAMP WITH TIME ZONE,
    retry_count INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    
    CONSTRAINT outbox_events_status_check CHECK (status IN ('PENDING', 'PROCESSING', 'SENT', 'FAILED', 'DEAD_LETTERED', 'DISCARDED'))
);

-- Rule 87: 동시성 제어를 위한 인덱스
CREATE INDEX idx_outbox_events_status_created_at ON outbox_events (status, created_at);
CREATE INDEX idx_outbox_events_pending_processing ON outbox_events (status, last_attempt_time, created_at) 
WHERE status IN ('PENDING', 'PROCESSING');
```

## 에러 처리

**Rule 80**: 독립 모듈의 자체 예외 및 에러 코드 체계

모든 에러는 `OutboxException` 클래스를 통해 처리됩니다:

- `MessageNotFoundException`: 메시지 조회 실패
- `MessageProcessingFailedException`: 메시지 처리 실패
- `KafkaSendFailedException`: Kafka 전송 실패
- `DatabaseOperationException`: 데이터베이스 작업 실패
- `MaxRetriesExceededException`: 최대 재시도 횟수 초과
- `UnexpectedInfrastructureException`: 예상치 못한 인프라 에러

## 모니터링

**Rule VII.2.23**: 다음 지표들을 자동으로 모니터링합니다:

- 상태별 메시지 수 (PENDING, PROCESSING, SENT, FAILED, DEAD_LETTERED)
- 메시지 처리 시간
- 재시도 횟수 분포
- 정리 대상 메시지 수

로그를 통해 메트릭이 주기적으로 출력되며, 임계값 초과 시 경고 로그가 생성됩니다.

## 주요 규칙 준수사항

- **Rule 80**: 완전한 독립성 - 다른 프로젝트 모듈에 의존하지 않음
- **Rule 87**: 동시성 제어 - FOR UPDATE SKIP LOCKED 사용
- **Rule VII.1**: Avro4k 방법론 - ByteArray 페이로드 처리
- **Rule 86**: 주기적 폴링 - 1초마다 대기 메시지, 5분마다 실패 메시지 재시도
- **Rule 90**: 지수 백오프 재시도 정책
- **Rule VII.2.23**: 자동 모니터링 및 메트릭 수집

## 주의사항

1. **트랜잭션 관리**
   - 도메인 로직과 이벤트 저장이 같은 트랜잭션에서 실행되어야 합니다.
   - `@Transactional` 어노테이션을 적절히 사용하세요.

2. **성능**
   - 배치 크기와 폴링 간격을 적절히 조정하세요.
   - 메시지 크기가 너무 크지 않도록 주의하세요.

3. **모니터링**
   - 실패한 메시지와 Dead Letter Queue를 주기적으로 모니터링하세요.
   - 로그 레벨을 적절히 설정하세요.

4. **보안**
   - 민감한 정보는 암호화하여 저장하세요.
   - 적절한 접근 제어를 설정하세요.

5. **독립성 유지**
   - 이 모듈은 다른 도메인 모듈에 의존하지 않도록 설계되었습니다.
   - 다른 프로젝트에 이식할 때는 최소한의 설정 변경만 필요합니다.
