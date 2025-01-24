.
├── domains/                             # 도메인 단위 최상위 폴더
│   ├── user/                            # User 도메인
│   │   ├── presentation/           # [모듈] API 계층
│   │   │   └── src/main/kotlin/
│   │   │       └── com/company/user/presentation/
│   │   │           ├── controller/
│   │   │           │   ├── command/     # Command API
│   │   │           │   │   └── UserCommandController.kt
│   │   │           │   └── query/       # Query API
│   │   │           │       └── UserQueryController.kt
│   │   │           ├── dto/
│   │   │           │   ├── request/
│   │   │           │   │   ├── CreateUserRequest.kt
│   │   │           │   │   └── UpdateUserRequest.kt
│   │   │           │   └── response/
│   │   │           │       └── UserDetailResponse.kt
│   │   │           └── config/
│   │   │               └── WebSecurityConfig.kt
│   │   │
│   │   ├── application/            # [모듈] CQRS 핵심
│   │   │   └── src/main/kotlin/
│   │   │       └── com/company/user/application/
│   │   │           ├── command/
│   │   │           │   ├── handler/     # CommandHandler 구현체
│   │   │           │   │   ├── CreateUserHandler.kt
│   │   │           │   │   └── UpdateUserHandler.kt
│   │   │           │   ├── service/     # Command 서비스
│   │   │           │   │   └── UserCommandService.kt
│   │   │           │   └── dto/
│   │   │           │       └── UserCommandDto.kt
│   │   │           ├── query/
│   │   │           │   ├── handler/     # QueryHandler 구현체
│   │   │           │   │   └── GetUserHandler.kt
│   │   │           │   ├── service/     # Query 서비스
│   │   │           │   │   └── UserQueryService.kt
│   │   │           │   └── dto/
│   │   │           │       └── UserQueryDto.kt
│   │   │           └── port/            # Hexagonal Ports
│   │   │               ├── UserRepositoryPort.kt
│   │   │               └── EventPublisherPort.kt
│   │   │
│   │   ├── domain/                 # [모듈] 도메인 코어
│   │   │   └── src/main/kotlin/
│   │   │       └── com/company/user/domain/
│   │   │           ├── model/           # Aggregate Root
│   │   │           │   ├── User.kt
│   │   │           │   └── UserId.kt
│   │   │           ├── vo/           # Value Objects
│   │   │           │   └── Email.kt
│   │   │           ├── event/           # Domain Events
│   │   │           │   └── UserCreatedEvent.kt
│   │   │           └── exception/       # Domain Exceptions
│   │   │               └── UserValidationException.kt
│   │   │
│   │   └── infrastructure/         # [모듈] 인프라 구현체
│   │       └── src/main/kotlin/
│   │           └── com/company/user/infrastructure/
│   │               ├── adapter/
│   │               │   ├── persistence/ # DB 어댑터
│   │               │   │   ├── UserJpaRepository.kt
│   │               │   │   └── entity/  # JPA 엔티티
│   │               │   │       └── UserEntity.kt
│   │               │   └── messaging/   # 메시징 어댑터
│   │               │       └── KafkaEventPublisher.kt
│   │               ├── config/          # 인프라 설정
│   │               │   ├── JpaConfig.kt
│   │               │   └── KafkaConfig.kt
│   │               └── repository/      # 인프라 저장소
│   │                   └── UserRepositoryImpl.kt
│   │
│   └── order/                           # Order 도메인 (동일 구조)
│
├── libraries/                           # 독립 배포 라이브러리
│   ├── outbox/      # [모듈] Outbox 패턴 구현
│   │   └── src/main/kotlin/
│   │       └── com/company/outbox/
│   │           ├── config/              # 자동 설정
│   │           │   ├── OutboxAutoConfiguration.kt
│   │           │   └── OutboxProperties.kt
│   │           ├── domain/              # Outbox 엔티티
│   │           │   └── OutboxEvent.kt
│   │           ├── scheduler/           # 이벤트 발행 배치
│   │           │   └── OutboxScheduler.kt
│   │           └── repository/          # Outbox 저장소
│   │               └── OutboxEventRepository.kt
│   │
│   └── kafka-commons/                   # [모듈] Kafka 공통 기능
│       └── src/main/kotlin/
│           └── com/company/kafka/
│               ├── config/              # 프로듀서/컨슈머 설정
│               │   ├── KafkaProducerConfig.kt
│               │   └── KafkaConsumerConfig.kt
│               ├── serializer/          # 커스텀 직렬화
│               │   └── AvroSerializer.kt
│               └── error/               # 에러 처리
│                   └── DeadLetterProducer.kt
│
└── shared/                              # 공통 모듈
    ├── shared-common/                   # [모듈] 공통 기반
    │   └── src/main/kotlin/
    │       └── com/company/common/
    │           ├── exception/           # 공통 예외
    │           │   └── GlobalExceptionHandler.kt
    │           └── util/                # 유틸리티
    │               └── DateTimeUtils.kt
    │
    ├── shared-events/                   # [모듈] 이벤트 계약
    │   └── src/main/kotlin/
    │       └── com/company/events/
    │           └── user/                # 도메인별 이벤트
    │               └── UserCreatedEvent.kt
    │
    └── shared-utils/                    # [모듈] 확장 기능
        └── src/main/kotlin/
            └── com/company/utils/
                ├── extensions/          # 확장 함수
                │   └── StringExtensions.kt
                └── coroutine/           # 코루틴 유틸
                    └── CoroutineDispatcherProvider.kt