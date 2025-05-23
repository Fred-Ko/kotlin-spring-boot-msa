---
trigger: always_on
description: 
globs: 
---
## 프로젝트 기술 문서 규칙

### 프로젝트 구조

1.  **모든 규칙의 필수 준수**: 프로젝트 기술 문서에 명시된 모든 규칙과 패턴은 선택 사항이 아닌 필수이며, 모든 구성원은 예외 없이 이를 준수해야 한다. 규칙 위반 시 즉시 코드 리뷰에서 반려된다. **단, 극히 예외적인 상황에서 규칙 준수가 프로젝트 목표 달성에 심각한 방해가 된다고 판단될 경우, 아키텍처 위원회(또는 지정된 검토 그룹)의 공식적인 승인을 통해 제한적으로 예외를 허용할 수 있다.**
2.  **레이어별 모듈 분리**: 프로젝트는 반드시 Presentation, Application, Domain, Infrastructure 레이어로 모듈을 분리해야 하며, 각 레이어는 별도의 Gradle 모듈로 관리된다. **`domains/` 디렉토리 아래의 모든 모듈 ( `domains/common` 포함)은 이 4계층 아키텍처 구조를 따라야 합니다. 각 레이어는 해당 모듈 내에서 규칙 141에 정의된 명확한 패키지 및 디렉토리 구조를 가져야 합니다.** **Infrastructure 레이어는 책임(예: 영속성, 메시징)에 따라 `infrastructure/persistence`, `infrastructure/messaging`과 같이 하위 모듈로 분리될 수 있다.**
3.  **도메인별 모듈화**: 각 비즈니스 도메인은 `domains/` 최상위 폴더 아래 독립적인 폴더로 구성해야 한다 (예: `domains/user`, `domains/order`). 도메인 간 직접적인 의존성은 명시적으로 금지된다.
4.  **공통 모듈 및 독립 모듈 관리**: 공통 유틸리티, 공유 추상 클래스, 인터페이스 (`DomainEvent`, `AggregateRoot` 등), 공통 예외 및 ErrorCode 등 시스템 전반에 걸쳐 사용되는 **기반** 개념은 `domains/common` 모듈에 포함되며, 특정 도메인 로직을 포함하지 않아야 한다. **이 `domains/common` 모듈은 다른 특정 도메인 모듈(예: `domains/user`)이나 `independent/` 하위 모듈에 의존해서는 안 됩니다.** `domains/common` 모듈 내의 코드들은 Rule 141에 따라 해당 코드가 속하는 아키텍처 레이어(Domain, Application, Presentation, Infrastructure 및 그 하위 모듈)에 따른 패키지 및 디렉토리 구조 내에 위치해야 합니다. 예를 들어, 공통 AggregateRoot는 `domains/common/domain/aggregate/` 패키지에, 공통 GlobalExceptionHandler는 `domains/common/presentation/` 패키지 아래에 위치해야 합니다. `independent/` 폴더 아래에 위치하는 독립 모듈(예: `independent/outbox`)은 특정 기술적 기능이나 크로스 커팅 관심사를 담당하며, 이 독립 모듈은 **프로젝트 내의 어떤 특정 도메인 모듈 또는 `domains/common` 모듈에도 의존하지 않습니다 (규칙 9 참조).** **독립 모듈들은 자체적으로 완전하고(self-contained) 다른 프로젝트에 최소한의 수정으로 이식 및 재사용 가능하도록 설계되어야 합니다.** **다른 도메인 모듈(예: `domains/user`)은 필요에 따라 `domains/common` 모듈의 공통 요소와 `independent/` 모듈의 Application Port 인터페이스를 의존하여 사용할 수 있습니다.** 공통 모듈 의존은 최소화하여 "공통 모듈 지옥"을 방지한다. `independent/` 모듈은 Rule 141의 패키지/디렉토리 구조 규칙을 따르지 않으며, 자체적인 구조 규칙(Rule 80 참조)을 정의하고 사용합니다.
5.  **Extensions 폴더 구조 및 사용 원칙**: 각 레이어(Presentation, Application, **Infrastructure의 하위 모듈들**)는 해당 레이어 관련 확장 함수를 모아두기 위한 `extensions` 폴더를 포함할 수 있다. 확장 함수는 해당 클래스가 정의된 모듈이나, 관련 기능이 집중된 레이어의 `extensions` 폴더에 위치시켜 코드의 논리적 흐름과 물리적 위치 간의 일관성을 고려한다. **Presentation 레이어의 경우, Command/Query, Request/Response DTO 변환 확장 함수는 Rule 141에 정의된 Presentation 레이어 경로(`domains/{domain}/presentation/...`) 내의 버전별 폴더(`v1/`, `v2/` 등) 바로 아래에 `extensions/` 폴더를 위치시키고, 그 하위에 Command, Query, Response DTO 구조에 따라 추가적인 하위 폴더(`command/dto/request/`, `query/dto/response/` 등)를 가지는 구조를 필수로 준수해야 한다. `extensions/` 폴더 자체는 버전별 폴더(`v1/`)의 하위에 단일 계층으로 정의되며, 추가적인 버전 접두사(예: `extensions/v1/`)를 포함하지 않는다.** 이 구조는 Rule 141.5에 정의된 Presentation 레이어 내 일반적인 `extensions` 하위 패키지 구조에 대한 **특수한 필수 하위 구조**입니다.
6.  **Extensions 기능별 폴더**: Rule 5에 정의된 각 `extensions` 하위 폴더는 해당 기능의 확장 함수만 포함하며, 다른 유형의 코드를 포함해서는 안 된다.
7.  **Extensions 파일명 규칙**: 확장 함수 파일명은 `도메인명+기능명+Extensions.kt` 형식을 따른다 (예: `UserMappingExtensions.kt`, `OrderValidationExtensions.kt`). **Presentation 레이어의 DTO 변환 확장 함수 파일명은 Rule 5 및 7에 따라 해당 구조를 반영하여 `UserCommandRequestExtensions.kt`, `UserProfileQueryResponseExtensions.kt` 등으로 명명해야 한다.**
8.  **확장 함수 위치**: 확장 함수는 가능한 해당 클래스가 정의된 모듈이나, 해당 기능을 확장하는 레이어의 `extensions` 폴더에 위치시킨다. 특정 레이어에 강제로 제한하기보다 코드의 가독성과 응집성을 우선하여 결정한다. **단, Rule 5에 명시된 Presentation 레이어의 DTO 변환 확장 함수는 해당 규칙에 명시된 구조와 위치를 필수로 준수해야 한다.**
9.  **모듈 간 의존성 명확화**: 모듈 간 의존성은 단방향으로 제한된다.
    *   **계층 간 의존성:** Domain 레이어는 다른 레이어를 의존하지 않는다. Application 레이어는 Domain을 의존하며 Infrastructure의 구체적인 구현체 대신 Port 인터페이스를 의존한다. Infrastructure는 Domain만 의존 가능하며, **Infrastructure 내 하위 모듈(예: `persistence`)은 동일 도메인의 다른 Infrastructure 하위 모듈(예: `messaging`)에 정의된 컴포넌트(예: 이벤트 직렬화 팩토리)를 의존할 수 있다.**
    *   **`domains/common` 모듈의 의존성:** `domains/common` 모듈은 시스템 전반의 기반 개념을 제공하며, **다른 특정 도메인 모듈(예: `domains/user`, `domains/order`)이나 `independent/` 하위 모듈에 의존하지 않습니다.**
    *   **개별 도메인 모듈 (`domains/{domain-name}`)의 의존성:** 각 개별 도메인 모듈(예: `domains/user`, `domains/order`)은 필요에 따라 `domains/common` 모듈의 공통 요소와 독립 모듈(`independent/` 하위 모듈)의 **Application Port 인터페이스**를 의존하여 사용할 수 있습니다. **도메인 간 직접적인 의존성은 명시적으로 금지됩니다 (규칙 3 참조).**
    *   **독립 모듈 (`independent/`)의 의존성:** 독립 모듈(예: `independent/outbox` - Rule 80 참조)은 자체적인 레이어 구조를 가질 수 있으며, **프로젝트 내의 다른 어떤 모듈(다른 특정 도메인 모듈, `domains/common` 모듈, 심지어 다른 `independent` 모듈 포함)에도 의존하지 않도록 설계되어** 완전한 독립성과 이식성을 보장해야 합니다. 다른 모듈들은 이 독립 모듈의 **Application Port 인터페이스**만을 의존합니다.
    *   **기타 의존성 관리:** 모듈 간 `@ComponentScan`은 제한적으로 사용되며, 특히 Application 모듈의 테스트 설정 외에는 지양한다. 의존성 주입은 Spring `@Bean` 또는 `@Component`를 통해 명시적으로 관리한다.

### 핵심 개발 원칙

10. **도메인 레이어 독립성**: Domain 레이어 (`domains/{domain}/domain/` - Rule 141 준수)는 Presentation, Application, Infrastructure, 독립 모듈(`independent/`) 레이어를 절대 참조해서는 안 됩니다. 또한, Spring, JPA, Kafka 클라이언트, **HTTP 관련 API (`HttpStatus` 등)** 등 특정 **기술 구현 프레임워크 또는 프로토콜**에 대한 직접적인 의존성(로직, 어노테이션 포함)을 가져서는 안 됩니다. 하지만, 도메인 모델링 자체를 본질적으로 지원하는 **필수적인 표준 라이브러리 또는 유틸리티 라이브러리**(예: `kotlin-stdlib`, `java.time` API, 표준 Validation API - `jakarta.validation-api`, Guava 등 검증된 유틸리티)의 사용은 **최소한의 범위 내에서 신중하게 허용**될 수 있습니다. 이러한 라이브러리 사용 결정은 코드 리뷰를 통해 그 필요성과 도메인 순수성 침해 여부를 엄격히 검토해야 합니다. `domains/common` 모듈의 공통 도메인 요소(`AggregateRoot`, `DomainEvent` 인터페이스 등 - `domains/common/domain/` - Rule 141 준수)에 대한 의존성은 허용됩니다. Domain 모델의 고유 식별자는 UUID와 같은 기술 독립적인 타입의 Value Object로 캡슐화하여 사용해야 한다 (예: `UserId`, `OrderId`). 데이터베이스 자동 생성 식별자(예: Long)는 Infrastructure 레이어에서만 관리되며, Domain 모델에 노출되지 않는다.
11. **Aggregate, Entity, VO 위치 및 순수성**: Aggregate, Domain Entity, Value Object (VO)는 반드시 해당 도메인 모듈의 Domain 레이어 (`domains/{domain}/domain/` - Rule 141 준수)에 정의하며, **특정 기술 구현 프레임워크**(JPA, Spring 등)의 로직이나 어노테이션에 의존하지 않고 순수하게 유지한다. **이때, Domain Entity (Aggregate Root 포함)와 Value Object를 구분하는 핵심 기준은 '고유 식별자(identity)'의 유무이다. Domain Entity는 고유한 식별자를 가지며 자체적인 생명주기(lifecycle)를 통해 추적 및 변경 관리되는 반면, Value Object는 기술적인 식별자를 갖지 않고 오직 구성 속성들의 값으로만 정의된다.** 기술적 영속화 관련 상세(DB 자동 생성 ID 매핑, Fetch 전략 등)는 해당 도메인 모듈의 **Infrastructure의 영속성 담당 모듈(예: `infrastructure/persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)** 의 JPA Entity에만 정의한다. **Aggregate는 불변 객체로 설계한다.**
11.5. **Aggregate Root 식별자 (ID) 정의**: 모든 Aggregate Root 클래스는 해당 Aggregate의 고유 식별자를 나타내는 필드를 가져야 하며, 이 필드는 Rule 10에 따라 Domain ID Value Object 타입으로 정의되어야 한다. 이 식별자 필드는 `val`로 선언되어야 하며, 생성 시점에 할당된 이후 변경될 수 없다. 해당 필드는 외부 레이어(Application, Infrastructure)에서 Aggregate를 식별하고 참조할 수 있도록 `public val` 또는 `internal val`로 노출해야 한다.
12. **Aggregate VO 사용 제한**: 도메인 내 Aggregate의 Value Object는 Domain 레이어 외부에서 참조하거나 직접 사용하지 않아야 한다. 외부 레이어에서는 DTO로 변환하여 사용한다.
13. **VO 불변성 및 생성 제어**: 모든 VO는 불변 객체로 설계하며, `private` 생성자와 `companion object`의 `of` 메서드 (또는 `ofString` 등 의미있는 정적 팩토리 메서드) 를 통해 생성을 제어해야 한다.
14. **VO 유효성 검사**: VO의 유효성 검사는 `init` 블록 또는 팩토리 메서드에서 수행하며, 유효성 검사 실패 시 해당 도메인 레이어에 정의된 **`DomainException`을 상속하는 특정 유효성 검사 예외 타입** (Rule 68 참조)을 던져야 한다. 이러한 유효성 검사 예외는 Rule 68에 명시된 Validation 관련 베이스 예외 타입(예: `UserDomainException.Validation`)을 상속해야 한다. **표준 Validation API 어노테이션(`jakarta.validation.constraints.*`)을 VO 필드에 명시하는 것은 허용될 수 있으나, 실제 검증 로직 실행 및 예외 발생 책임은 여전히 `init` 또는 팩토리 메서드 내에 있어야 합니다.**
15. **VO toString 오버라이드**: VO의 `toString()` 메서드는 디버깅 및 로깅을 위해 값 자체를 명확히 표현하도록 재정의하거나, 민감 정보(예: 비밀번호)는 보안을 위해 마스킹 처리할 수 있다. (예: `Password.toString()`은 `********` 반환)
16. **Aggregate 팩토리 함수**: Aggregate 및 Domain Entity는 `companion object`에 `create` (신규 생성) 및 `reconstitute` (영속성 로딩 후 재구성) 팩토리 함수를 정의해야 한다. `reconstitute`는 Domain 식별자 Value Object를 인자로 받아야 한다. **`reconstitute` 팩토리 함수는 오직 해당 도메인 모듈의 Infrastructure의 영속성 담당 모듈(예: `infrastructure/persistence` - Rule 141 준수) 내 Repository 구현체 내부에서, 데이터베이스 등 영속성 저장소로부터 읽어온 데이터(예: JPA Entity)를 기반으로 Domain Aggregate 객체를 메모리 상에 재구성(rehydrate)할 목적으로만 사용되어야 한다.** Application 레이어나 다른 Domain 객체 내에서 이 메서드를 직접 호출해서는 안 된다. `create` 팩토리 함수는 애그리거트의 초기 상태를 생성하며, 이 과정에서 발생하는 초기 상태 변경(`UserCreated` 등)에 대한 `DomainEvent`를 애그리거트 내부의 이벤트 목록에 `addDomainEvent()` 메서드를 통해 추가한다.
17. **공통 AggregateRoot 상속**: 모든 도메인 Aggregate 클래스는 `domains/common` 모듈의 domain 레이어 (`domains/common/domain/` - Rule 141 준수)에 정의된 추상 클래스 `AggregateRoot`를 상속받아야 한다.
18. **AggregateRoot 이벤트 관리**: `AggregateRoot` 베이스 클래스는 발생한 `DomainEvent` 객체들을 저장하기 위한 컬렉션(예: `private val domainEvents: MutableList<DomainEvent> = mutableListOf()`)을 내부에 가져야 하며, 이벤트를 추가하는 `addDomainEvent(event: DomainEvent)`, 읽기 전용 목록을 노출하는 `fun getDomainEvents(): List<DomainEvent>`, 이벤트 목록을 초기화하는 `fun clearDomainEvents()` 메서드를 제공해야 한다. **`addDomainEvent(event: DomainEvent)` 메서드는 `internal` 가시성을 가져야 하며, Aggregate의 상태 변경 메서드나 `create` 팩토리 함수 내에서만 호출되어야 한다.** Immutable Aggregate의 상태 변경 메서드는 변경된 Aggregate의 *새로운 인스턴스*를 반환한다. 이 새로운 인스턴스는 해당 변경으로 인해 발생한 이벤트를 자신의 내부 이벤트 목록에 포함해야 한다. `create` 팩토리 함수도 초기 상태 변경에 대한 `DomainEvent`를 생성하여 `addDomainEvent()`를 호출한다. Repository 구현체는 애그리거트 저장 전 최종 Aggregate 인스턴스의 `getDomainEvents()`로 이벤트를 수집하고 Rule 85에 따라 Outbox 저장을 준비한다. 저장 성공 후 `clearDomainEvents()`를 호출한다.
    ```kotlin
    // 예시: User Aggregate 내에서 이름을 변경하는 메서드 (Immutable)
    fun changeName(newName: String): User {
        // 유효성 검사 등 로직 수행...
        val updatedUser = this.copy( // Kotlin data class의 copy 활용
            name = newName,
            // 변경과 관련된 다른 필드 업데이트...
            version = this.version + 1 // 예시: 버전 관리
        )
        // 상태 변경에 따른 도메인 이벤트 발생
        updatedUser.addDomainEvent(UserEvent.NameChanged(userId = this.id, newName = newName))
        return updatedUser // 변경된 '새로운' 인스턴스 반환
    }

    // AggregateRoot 내 addDomainEvent (internal 가시성) - domains/common/domain/aggregate/AggregateRoot.kt (Rule 141 준수)
    internal fun addDomainEvent(event: DomainEvent) {
        this.domainEvents.add(event)
    }
    ```
19. **JPA 엔티티 위치**: JPA 엔티티는 반드시 해당 도메인 모듈의 **Infrastructure 영속성 담당 모듈(예: `infrastructure/persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)의 `entity` 패키지**에 정의하며, 도메인 로직을 포함하지 않는다. `independent/outbox` 등 독립 모듈의 JPA 엔티티는 해당 모듈의 Infrastructure 레이어 (`independent/outbox/infrastructure/entity/` - Rule 80 참조, Rule 141 미적용)에 정의한다. JPA 엔티티는 Domain 모델과 1:1로 매핑되지 않고 영속화 구조를 반영한다. Domain ID (Value Object)와 JPA Long ID는 Infrastructure에서 매핑된다.
20. **JPA 엔티티 불변성 및 Lazy Loading 공존**: JPA 엔티티의 기본 필드는 가능한 `val`로 선언하여 불변성을 유지하며, Setter 메서드는 절대 사용하지 않는다. 연관 관계 필드는 `@OneToMany`, `@ManyToOne` 등에 `fetch = FetchType.LAZY`를 설정한다. Hibernate 프록시를 통한 지연 로딩 시 Kotlin `val` 필드 접근 문제가 발생할 수 있으므로, 영속성 컨텍스트 외부에서 사용하거나 성능 최적화가 필요한 경우 Projection (인터페이스 기반 DTO) 또는 EntityGraph를 사용하여 필요한 데이터를 명시적으로 조회하도록 설계한다. `lateinit var`나 `@JvmField` 사용은 최소화하고 신중하게 적용한다. `kotlin-jpa` 및 `kotlin-allopen` 플러그인을 반드시 적용한다. JPA 엔티티 클래스 자체에는 `@ConsistentCopyVisibility` 어노테이션을 사용하지 않는다.
21. **JPA 엔티티 ID 필드**: JPA 엔티티는 DB 자동 생성 키에 매핑되는 `val id: Long? = null` 필드와 함께, Domain 모델의 고유 식별자(예: `UserId` Value Object가 캡슐화한 `UUID`)에 매핑되는 `val domainId: UUID`와 같은 필드를 포함해야 한다.
22. **JPA 엔티티 테이블명**: JPA 엔티티의 테이블명은 `@Table` 어노테이션으로 명시하며, 소문자 복수형을 사용한다 (예: `@Table(name = "users")`).
23. **JPA 엔티티 컬럼 속성**: JPA 엔티티의 컬럼은 `@Column` 어노테이션으로 제약 조건을 명시하며, 데이터베이스 스키마와 일치해야 한다 (예: `@Column(nullable = false, length = 50)`).
24. **JPA 엔티티와 도메인 매핑**: JPA 엔티티와 도메인 Aggregate/Entity/VO 간 변환은 해당 도메인 모듈 **Infrastructure 영속성 담당 모듈(예: `infrastructure/persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)의 `extensions` 폴더**에서 코틀린 확장 함수로 정의한다 (예: `UserEntity.toDomain()`). 이 변환 함수는 Domain ID (Value Object)와 Persistence ID (Long, UUID) 간의 매핑을 처리해야 한다.
25. **JPA 매핑 파일명**: JPA 매핑 확장 함수 파일은 `{Domain}EntityExtensions.kt` 형식을 따라야 한다 (예: `UserEntityExtensions.kt`).
26. **JPA 관계 설정**: JPA 엔티티의 관계는 단방향을 우선하며, 양방향 관계는 명확한 필요성(예: 비즈니스 요구사항)이 있을 때만 사용한다.
27. **JPA Fetch 전략**: JPA 엔티티 관계는 기본적으로 `@ManyToOne`, `@OneToMany` 등에 `fetch = FetchType.LAZY`를 설정하며, Eager 로딩은 성능 검증 후 사용한다. Repository 메서드에 `@EntityGraph`를 사용하여 필요한 연관 엔티티를 함께 로딩한다.
28. **JPA Kotlin 플러그인**: `kotlin-jpa`와 `kotlin-allopen` 플러그인을 반드시 적용하여 JPA 요구사항을 충족한다.
29. **규칙 29 삭제됨**.
30. **JPA 도메인 로직 배제**: JPA 엔티티에는 비즈니스 로직을 포함시키지 않으며, 로직은 Domain 레이어의 Aggregate 또는 Domain Entity에서 처리한다.
31. **JPA 동시성 제어**: 모든 JPA 엔티티는 낙관적 잠금을 기본으로 사용하며, `@Version` 어노테이션을 필수로 포함하여 동시성 충돌을 관리한다. 낙관적 잠금 실패 시 발생하는 `OptimisticLockException`은 상위 레이어(Presentation/GlobalExceptionHandler)로 전파되어 일관된 응답으로 변환된다.
32. **공통 DomainEvent 인터페이스 정의**: 모든 도메인 이벤트가 구현해야 할 `DomainEvent` 인터페이스는 `domains/common` 모듈의 domain 레이어 패키지(`domains/common/domain/event/DomainEvent.kt` - Rule 141 준수)에 정의하며, 기술 독립적이어야 한다. `independent/outbox` 모듈은 이 인터페이스를 **직접 의존하지 않는다** (Rule 80 참조, Rule 141 미적용). 도메인 Infrastructure 레이어는 이 인터페이스를 구현하는 구체적인 이벤트를 처리한다.
33. **도메인 이벤트 정의 위치**: 도메인 이벤트 클래스는 해당 도메인 모듈의 `domain/event/` 패키지 (`domains/{domain}/domain/event/` - Rule 141 준수)에 정의하며, 순수 Kotlin `data class`로 구현하고 `DomainEvent` 인터페이스를 구현해야 한다. 이벤트 페이로드는 불변 타입 및 불변 컬렉션(`List`, `Map` 등)만을 포함해야 한다.
34. **Aggregate별 이벤트 그룹화**: 각 Aggregate (또는 도메인 엔티티)와 관련된 모든 도메인 이벤트는 해당 Aggregate의 이름 뒤에 `Events`를 붙인 파일명(예: `UserEvent.kt`)으로, 해당 도메인 모듈의 `domain/event/` 패키지 (`domains/{domain}/domain/event/` - Rule 141 준수) 내에 정의한다. 이벤트들은 하나의 `sealed class`로 상위 타입을 정의하고 (예: `sealed class UserEvent(...) : DomainEvent`), 실제 발행 이벤트들은 해당 `sealed class` 내부에 `data class`로 중첩하여 구현한다 (예: `data class UserEvent.Created(...) : UserEvent(...)`). 중첩된 이벤트는 상위 `sealed class`의 생성자를 통해 공통 필드(예: `occurredAt`, 관련 Aggregate 식별자 Value Object 등)를 상속받아 관리한다. 특히 `userId`와 같은 관련 Aggregate 식별자는 상위 `sealed class UserEvent`의 추상 프로퍼티나 생성자 인자로 정의하고, 하위 이벤트 데이터 클래스들이 이를 상속받아 사용한다. 이벤트 페이로드에는 Infrastructure-specific ID(예: `Long`)를 포함하지 않고 Domain적인 의미를 가지는 데이터만 포함한다.

### API 개발 규칙

35. **Command 요청 동기 응답**: Command 요청의 동기 응답은 JSON 형식으로 `status` (예: "SUCCESS"), `message` (결과 메시지), `correlationId` (요청 추적 ID)를 필수로 포함한다. Command 실행 성공 응답은 Common 모듈의 Presentation 레이어에 정의된 `CommandResultResponse` DTO (`domains/common/presentation/dto/response/CommandResultResponse.kt` - Rule 141 준수)를 사용하며, Rule 39에 따라 HATEOAS 링크를 포함한다.
36. **Command 요청 비동기 응답**: 비동기 Command 응답은 `status`, `message`, `jobId` (작업 ID), 상태 조회용 HATEOAS 링크(예: `/jobs/{jobId}/status`)를 포함한다.
37. **X-Correlation-Id 헤더**: 모든 API 요청은 `X-Correlation-Id` 헤더를 필수로 포함하며, Controller는 이를 `correlationId`로 Application Layer에 전달한다. `correlationId`는 Application Layer 및 하위 레이어에서 요청 처리 전반에 걸쳐 추적 목적으로 사용되어야 한다. **`correlationId`를 하위 레이어에 전달/관리하는 방식(명시적 파라미터 전달 또는 MDC 등 암묵적 메커니즘)은 현재 결정되지 않았으며, 추후 표준을 정의해야 한다.**
38. **HATEOAS 의존성**: API는 `spring-boot-starter-hateoas`를 의존성으로 추가하며, 모든 성공적인 응답은 HATEOAS 링크를 포함한다.
39. **HATEOAS 링크 구조**: HATEOAS 링크는 `rel` (링크 관계), `href` (URI), `method` (HTTP 메서드)를 포함하며, `_links` 필드에 `List<Link>` 형태로 반환된다. Command 성공 응답 및 Query 응답 DTO는 Spring HATEOAS의 `RepresentationModel`을 상속받거나 래핑하여 `_links` 필드를 제공한다.
40. **HATEOAS URI 동적 생성**: HATEOAS 링크는 하드코딩 대신 Spring HATEOAS의 `linkTo`와 `methodOn`을 사용해 동적으로 생성한다 (예: `linkTo(methodOn(UserController::class.java).getUser(id)).withSelfRel()`).
41. **비동기 Command 상태 링크**: 비동기 Command 응답은 `job-status` 링크를 포함하며, 이는 `/jobs/{jobId}/status` 형식으로 `jobId`를 기반으로 생성된다.
42. **다건 조회 페이지네이션**: 다건 조회 API는 반드시 페이지네이션을 구현하며, 응답은 `content`, `page`, `size`, `totalElements`, `totalPages` 필드를 포함한다.
43. **커서 페이지네이션 우선**: 페이지네이션은 커서 기반 페이지네이션을 기본으로 사용하며, 오프셋 페이지네이션은 성능 검증 후 사용한다.
44. **Presentation Validation**: 해당 도메인 모듈의 Presentation 레이어 (`domains/{domain}/presentation/` - Rule 141 준수)에 정의된 `Request DTO`의 유효성 검사는 `jakarta.validation.constraints` 어노테이션(예: `@NotNull`, `@Size`)을 사용하여 수행한다.
45. **Validation 의존성**: 프로젝트는 `spring-boot-starter-validation`을 의존성에 반드시 추가한다.
46. **Request DTO Validation**: 모든 해당 도메인 모듈의 Presentation 레이어 (`domains/{domain}/presentation/` - Rule 141 준수)에 정의된 `Request DTO` 필드는 적절한 검증 어노테이션을 반드시 적용하며, 검증 로직은 어노테이션으로 처리한다.
47. **Controller `@Valid` 사용**: Controller 메서드는 `Request DTO`에 `@Valid` 어노테이션을 적용하여 유효성 검사를 트리거한다.
48. **Validation 에러 응답**: Validation 실패 시 RFC 9457 `ProblemDetail` 형식을 따르며, `invalid-params` 필드에 필드별 에러 메시지(예: `[{ "field": "name", "reason": "cannot be empty" }]`를 포함한다.
49. **에러 응답 형식**: 모든 에러 응답은 RFC 9457을 준수하며, `ProblemDetail` 클래스를 사용하여 `type`, `title`, `detail`, `errorCode`, `timestamp`, **`correlationId`**를 포함해야 한다.
50. **ProblemDetail 속성 설정**: `ProblemDetail`은 `.apply { ... }` 람다 블록으로 속성을 설정하며, `ResponseEntity.status(determinedHttpStatus).body(problemDetail)`로 반환한다. HTTP 상태 코드는 Common 모듈의 GlobalExceptionHandler (`domains/common/presentation/GlobalExceptionHandler.kt` - Rule 141 준수)가 발생한 예외(및 ErrorCode)에 기반하여 **자체 매핑 로직으로 결정한 값(`determinedHttpStatus`)**을 사용한다. `ProblemDetail` 객체 생성 시 `correlationId` 필드를 필수로 설정해야 한다.
51. **GlobalExceptionHandler 구현**: 모든 프로젝트는 Common 모듈의 Presentation 레이어 (`domains/common/presentation/GlobalExceptionHandler.kt` - Rule 141 준수)에 `GlobalExceptionHandler` 클래스를 구현하며, 다음 예외를 반드시 처리한다: `MethodArgumentNotValidException` (규칙 48 상세 포함), `HttpMessageNotReadableException`, `DomainException`, `ApplicationException`, `OptimisticLockException`, `Exception`. GlobalExceptionHandler는 처리하는 모든 예외에 대해 Rule 49, 50에 따라 `ProblemDetail` 응답을 생성하며, 이때 `correlationId`를 포함해야 한다.
52. **컨트롤러 및 글로벌 예외 처리**: 컨트롤러 레벨(`@ExceptionHandler` in ControllerAdvice) 예외 처리는 특정 컨트롤러에 국한되는 경우에만 제한적으로 사용하고, DomainException, ApplicationException, OptimisticLockException을 포함한 대부분의 예외 처리는 Common 모듈의 GlobalExceptionHandler (`domains/common/presentation/GlobalExceptionHandler.kt` - Rule 141 준수)에서 수행하여 일관된 응답 형식을 유지한다. DomainException과 ApplicationException은 Application 레이어에서 잡지 않고 Presentation/GlobalExceptionHandler까지 전파되어 일관된 응답 형식으로 변환된다.
53. **API 문서화 Swagger**: API 문서화는 `springdoc-openapi` 라이브러리를 사용하여 Swagger로 구현한다.
54. **Swagger 어노테이션**: Controller의 모든 엔드포인트는 `@Operation` (설명), `@ApiResponse` (응답), `@Parameter` (파라미터) 어노테이션을 적용한다.
55. **Swagger 에러 응답**: Swagger 문서는 RFC 9457 `ProblemDetail` 형식을 반영하며, 에러 응답 스키마에 `correlationId`를 포함하여 명시한다.
56. **Swagger HATEOAS 링크**: Swagger 문서는 HATEOAS 링크 구조를 포함하는 응답 DTO (`RepresentationModel` 상속 등)의 스키마를 정확히 반영한다.
57. **API 버전 관리**: API는 해당 도메인 모듈의 Presentation 레이어 경로 (`domains/{domain}/presentation/` - Rule 141 준수) 하위에 `/v1/` 접두사를 사용하여 버전을 관리하며, 새로운 주요 변경 시 버전을 증가시킨다 (예: `/v2/`).

### 데이터 처리 규칙

58. **DTO 변환**: Presentation 레이어의 `Request DTO`는 Application 레이어의 `Command DTO` 또는 `Query DTO`로 변환되며, 변환은 Presentation 레이어의 `extensions` 폴더 (`domains/{domain}/presentation/.../extensions/` - Rule 141 및 Rule 5 준수)의 확장 함수로 구현한다. **이 변환 함수는 Rule 5에 명시된 구조와 위치를 필수로 준수해야 한다.**
59. **DTO 변환 파일명**: DTO 변환 확장 함수 파일은 `{Domain}DtoExtensions.kt` 또는 `{Domain}RequestExtensions.kt` 형식을 따를 수 있다. **Presentation 레이어의 경우, Rule 5 및 7에 따라 Command/Query/Response DTO 구조를 반영하여 `UserCommandRequestExtensions.kt`, `UserProfileQueryResponseExtensions.kt` 등으로 명명해야 한다.**
60. **데이터 변환 일관성**: 모든 데이터 변환(예: DTO, 엔티티, Domain Entity/VO 간)은 확장 함수를 사용하며, 변환 로직은 단일 책임 원칙을 준수해야 한다. **Infrastructure 레이어의 영속성 담당 모듈(`persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)** 변환 함수는 Domain ID (Value Object)와 Persistence ID (Long, UUID) 매핑을 처리해야 한다. **또한 Infrastructure의 메시징 담당 모듈(`messaging` - `domains/{domain}/infrastructure/messaging/` - Rule 141 준수)은 Rule 85에 따라 Domain Event를 Kafka 메시지 페이로드(Avro bytes 등)로 직렬화하는 역할을 담당한다.**
61. **VO 생성 규칙**: VO는 `companion object`의 `of` 메서드 (또는 `ofString` 등 의미있는 정적 팩토리 메서드) 로만 생성하며, `private` 생성자를 사용하여 직접 생성을 차단한다. VO 생성 시 발생하는 유효성 검사 실패는 Rule 14에 따라 해당 도메인 모듈의 **Domain Layer (`domains/{domain}/domain/exception/` - Rule 141 준수)에 정의된 InvalidInput 관련 `DomainException`을 상속하는 특정 유효성 검사 예외 타입** (Rule 68 참조)으로 발생되어 상위로 전파되어야 한다.
62. **JPA 엔티티 관계 Lazy 로딩**: JPA 엔티티 관계는 `FetchType.LAZY`를 기본으로 설정하며, 관계 접근 시 N+1 문제를 방지하기 위해 `@EntityGraph` 또는 JPQL, Projection을 사용한다. Repository 메서드에서 Domain 객체로 변환 시 연관 객체가 필요한 경우, `@EntityGraph`를 사용하여 Eager 로딩하고 Domain 객체로 변환한다.
63. **데이터베이스 스키마 일치**: JPA 엔티티의 `@Table`, `@Column` 정의는 데이터베이스 스키마와 1:1로 일치해야 하며, 스키마 변경 시 엔티티를 동기화한다.
64. **로깅 표준화**: 모든 레이어에서 로깅은 SLF4J를 사용하며, `correlationId`를 포함한 구조화된 로깅을 적용한다 (예: `log.info("Processing user creation, correlationId={}", correlationId)`).
65. **불변 컬렉션 사용 의무화**: Domain 및 Application 레이어의 모든 공개 메서드 (public, internal) 및 생성자는 `MutableList` 또는 `MutableMap` 타입을 반환하거나 파라미터로 사용해서는 안 된다. 대신 Kotlin의 불변 컬렉션 타입인 `List`, `Map` 또는 Guava의 `ImmutableList`, `ImmutableMap` 등과 같이 불변성이 보장된 컬렉션 타입을 사용해야 한다.
66. **Infrastructure 경계에서의 Mutable 컬렉션 사용**: Infrastructure 레이어에서 JPA 엔티티와 Domain 객체 또는 DTO 간의 데이터 변환이 발생하는 모듈 경계에서는 데이터 구조 변환을 위해 필요한 경우 mutable 컬렉션 타입의 사용이 제한적으로 허용될 수 있다. 하지만 변환 완료 후 Domain 또는 Application 레이어로 전달될 때는 반드시 불변 컬렉션으로 변환하여 전달해야 한다.

### 애플리케이션 핸들러 예외 처리 규칙 (sealed class & ErrorCode 규칙 적용)

67. **ErrorCode 인터페이스 및 구현체 정의**:
    *   **ErrorCode Interface**: 모든 에러 코드가 구현해야 할 `ErrorCode` 인터페이스는 `domains/common` 모듈의 **Domain 레이어** 내 **`error` 패키지** (`domains/common/domain/error/ErrorCode.kt` - Rule 141 준수)에 정의하며, 이 인터페이스는 **`val code: String` 및 `val message: String` 속성**만 정의한다.
    *   **Domain ErrorCode**: 모든 도메인 비즈니스 규칙 위반 관련 에러 코드는 각 도메인 모듈의 **Domain 레이어** 내 **`error` 패키지** (`domains/{domain}/domain/error/` - Rule 141 준수)에 정의된 Enum 클래스(예: `domains/user/domain/error/UserDomainErrorCodes.kt`)로 구현한다. 이 Enum은 Rule 67에 정의된 `ErrorCode` 인터페이스를 구현해야 하며, **`code: String` 및 `message: String` 필드**를 포함해야 한다. ErrorCode의 `code` 필드는 `{DOMAIN}-{LAYER}-{NUMBER}` 형태의 패턴을 따른다 (예: `USER-DOMAIN-001`). **HTTP 상태 코드 정보는 포함하지 않는다.**
    *   **Application ErrorCode**: Application 레이어에서 발생하는 기술적 오류(예: 외부 서비스 연동 실패 등)는 각 도메인 모듈의 **Application 레이어** 내 **`error` 패키지** (`domains/{domain}/application/error/` - Rule 141 준수)에 정의된 Enum(예: `domains/user/application/error/UserApplicationErrorCode.kt`)으로 정의한다. 이 Enum도 Rule 67에 정의된 `ErrorCode` 인터페이스를 구현해야 하며, **`code: String` 및 `message: String` 필드**를 포함해야 한다. ErrorCode의 `code` 필드는 `{DOMAIN}-{LAYER}-{NUMBER}` 형태의 패턴을 따른다 (예: `USER-APPLICATION-101`). **HTTP 상태 코드 정보는 포함하지 않는다.**
    *   **Common System ErrorCode**: 공통적인 시스템 오류(예: 동시성 충돌, 기본적인 입력 형식 오류 등)에 대한 공통 ErrorCode Enum은 `domains/common` 모듈의 **Domain 레이어** 내 **`error` 패키지** (`domains/common/domain/error/CommonSystemErrorCode.kt` - Rule 141 준수)에 정의한다. 이 Enum도 Rule 67에 정의된 `ErrorCode` 인터페이스를 구현해야 하며, **`code: String` 및 `message: String` 필드**를 포함해야 한다. ErrorCode의 `code` 필드는 `{DOMAIN}-{LAYER}-{NUMBER}` 형태의 패턴을 따른다 (예: `COMMON-DOMAIN-001` 또는 `COMMON-SYSTEM-500`). **HTTP 상태 코드 정보는 포함하지 않는다.** (이 에러 코드는 시스템 수준 오류지만, DomainException을 통해 전달될 수 있으므로 Domain 레이어에 위치시킨다.)
    *   **Independent Module ErrorCode**: `independent/outbox` 모듈과 같은 독립 모듈은 자체적인 ErrorCode Enum 및 체계를 해당 모듈의 **Infrastructure 레이어** 내 **`error` 패키지** (`independent/outbox/infrastructure/error/OutboxErrorCodes.kt` - Rule 80 참조, Rule 141 미적용) 내에 정의하고 사용한다. 독립 모듈의 ErrorCode는 `domains/common`의 `ErrorCode` 인터페이스를 구현하지 않을 수 있으며, 자체적으로 필요한 정보(예: 내부 처리 상태 코드)를 가질 수 있다.

    > **[ErrorCode 선택 가이드라인]**
    >
    > 예외 상황 발생 시 적절한 ErrorCode(및 Exception 타입)를 선택하기 위해 다음 질문을 고려하십시오:
    >
    > 1.  **핵심 비즈니스 규칙이나 정책 위반인가?** (예: 사용자 이름 중복, 잔고 부족, VO 유효성 검사 실패)
    >     *   **Yes** -> 해당 도메인의 `DomainException` 및 `DomainErrorCode` 사용 (예: `UserDomainException.DuplicateUsername`, `OrderDomainException.InsufficientBalance` - `domains/{domain}/domain/exception/`, `domains/{domain}/domain/error/` - Rule 141 준수)
    > 2.  **Application 레이어의 책임 범위 내 기술적 문제 또는 외부 요인인가?** (예: 외부 API 호출 실패 후 재시도 소진, 요청 DTO 파싱/변환 오류, 예상치 못한 복구 불가능 오류)
    >     *   **Yes** -> 해당 도메인의 `ApplicationException` 및 `ApplicationErrorCode` 사용 (예: `PaymentApplicationException.ExternalApiFailure`, `UserApplicationException.UnexpectedError` - `domains/{domain}/application/exception/`, `domains/{domain}/application/error/` - Rule 141 준수)
    > 3.  **여러 도메인에 걸쳐 발생 가능하거나 특정 도메인과 무관한 시스템 수준의 문제인가?** (예: 낙관적 잠금 충돌, 기본적인 입력 형식 오류(Presentation 단계 처리 후에도 발생 시), 인증/인가 실패)
    >     *   **Yes** -> `domains/common` 모듈의 공통 ErrorCode (`domains/common/domain/error/CommonSystemErrorCode.kt` - Rule 141 준수) 또는 관련 표준 예외 처리 메커니즘(예: `OptimisticLockException` 처리) 활용 고려. (단, `independent/` 모듈은 자체 ErrorCode 체계 사용 - `independent/{module}/infrastructure/error/` - Rule 80 참조, Rule 141 미적용)

68. **DomainException 및 ApplicationException 정의**:
    *   **Common Base DomainException**: 모든 도메인별 `DomainException` sealed class의 상위 타입 역할을 하는 추상 `DomainException` 클래스는 `domains/common` 모듈의 **Domain 레이어** 내 **`exception` 패키지** (`domains/common/domain/exception/DomainException.kt` - Rule 141 준수)에 정의한다. 이 추상 클래스는 Rule 67에 정의된 `ErrorCode`를 참조하는 `abstract val errorCode: ErrorCode` 프로퍼티를 포함해야 한다.
    *   **Common Base ApplicationException**: 모든 도메인별 `ApplicationException` sealed class의 상위 타입 역할을 하는 추상 `ApplicationException` 클래스는 `domains/common` 모듈의 **Application 레이어** 내 **`exception` 패키지** (`domains/common/application/exception/ApplicationException.kt` - Rule 141 준수)에 정의한다. 이 추상 클래스도 Rule 67에 정의된 `ErrorCode`를 참조하는 `abstract val errorCode: ErrorCode` 프로퍼티를 포함해야 한다.
    *   **Domain Exception**: 비즈니스 로직에서 발생한 예외는 각 도메인 모듈의 **Domain 레이어** 내 **`exception` 패키지** (`domains/{domain}/domain/exception/` - Rule 141 준수)에서 정의된 sealed class (예: `domains/user/domain/exception/UserDomainException.kt`)를 사용한다. 이 sealed class는 Rule 68에 정의된 Common Base `DomainException`을 상속해야 하며, 해당 도메인의 Domain ErrorCode (Rule 67에 정의된 Enum 구현체)를 `errorCode: ErrorCode` 프로퍼티를 통해 참조해야 한다. **Domain 모델에서 발생하는 유효성 검사 실패를 나타내는 예외들은 `UserDomainException.Validation`과 같이 `DomainException` 하위에 특정 베이스 예외 타입(sealed class 또는 interface)을 정의하고 이를 상속하는 하위 data class로 정의해야 한다.** Domain 모델에서 발생하는 유효성 검사 실패(VO `init`, Aggregate 메서드 내 `require` 등)는 Rule 14 및 61에 따라 이 `DomainException`을 상속하는 특정 유효성 검사 예외 타입을 직접 던져야 한다.
    *   **Application Exception**: Application 레이어 자체에서 발생하는 예외(예: Command/Query DTO 기반의 입력값 형식 오류 *전* 처리, 외부 서비스 호출 실패 시 Resilience4j 예외, 복구 불가능한 예상치 못한 시스템 오류 등 기술적 오류)는 각 도메인 모듈의 **Application 레이어** 내 **`exception` 패키지** (`domains/{domain}/application/exception/` - Rule 141 준수)에서 정의된 sealed class (예: `domains/user/application/exception/UserApplicationException.kt`)를 사용한다. 이 sealed class는 Rule 68에 정의된 Common Base `ApplicationException`을 상속해야 하며, 해당 도메인의 Application ErrorCode (Rule 67에 정의된 Enum 구현체)를 `errorCode: ErrorCode` 프로퍼티를 통해 참조해야 한다. Domain 로직 위반으로 인한 예외를 ApplicationException으로 래핑하지 않도록 한다.
    *   **Independent Module Exception**: `independent/outbox` 모듈과 같은 독립 모듈은 자체적인 `OutboxException` 베이스 클래스 및 하위 예외들을 해당 모듈의 **Infrastructure 레이어** 내 **`exception` 패키지** (`independent/outbox/infrastructure/exception/OutboxException.kt` - Rule 80 참조, Rule 141 미적용) 내에 정의하고 사용한다. 독립 모듈의 예외는 `domains/common`의 예외 클래스를 상속하지 않을 수 있다.
69. **Application 계층 예외 전파**: Application 레이어의 Command Handler와 Query Handler는 DomainException 및 그 하위 예외를 절대 `catch` 하거나 다른 예외로 변환하지 않는다. 비즈니스 규칙 위반 발생 시 DomainException은 Application 레이어를 그대로 통과하여 상위 계층(Presentation/GlobalExceptionHandler)으로 전파되어야 한다.
70. **Application 계층 자체 예외 처리**: Application 핸들러는 자신의 책임 범위 내에서 발생하는 문제(예: Command/Query DTO 기반의 입력값 형식 오류 *전* 처리, 외부 서비스 호출 실패 시 Resilience4j 예외 등 기술적 오류, 복구 불가능한 예상치 못한 시스템 오류)에 대해서만 해당 도메인 모듈의 `ApplicationException` sealed class (기술적 오류 관련 data class - `domains/{domain}/application/exception/` - Rule 141 준수)를 발생시키고 상위로 전파한다. Domain 로직 위반으로 인한 예외(예: VO `init` 블록 `require` 실패)를 ApplicationException으로 래핑하지 않도록 한다.
71. **예외 발생 시 ErrorCode 로깅**: Application 레이어에서 예외 발생 시 SLF4J를 사용하여 예외 정보, `correlationId`와 함께 발생한 예외 객체가 가진 `errorCode.code`를 명시적으로 로깅해야 한다 (예: `log.error("Failed to process command, correlationId={}, errorCode={}, error={}", correlationId, e.errorCode.code, e.message, e)`).
72. **예외 메시지 표준화**: 각 도메인 모듈의 `ApplicationException` 및 `DomainException` sealed class와 하위 data class (`domains/{domain}/{layer}/exception/` - Rule 141 준수)는 해당 예외가 가진 `ErrorCode` (Enum)를 참조하여 사용자 친화적인 메시지와 개발자용 디버깅 메시지를 관리하며, 메시지는 단일 언어로 명확히 정의한다. 각 예외 클래스의 기본 메시지는 연결된 `errorCode.message`를 사용하고, 필요시 오버라이드하여 상세 정보를 추가할 수 있다.
73. **Presentation/Global 예외 처리 및 HTTP 상태 코드 매핑 책임**: Common 모듈의 Presentation 레이어에 위치한 `ControllerAdvice` 또는 `GlobalExceptionHandler` (`domains/common/presentation/GlobalExceptionHandler.kt` - Rule 141 준수)는 Application 레이어에서 전파된 `DomainException`과 `ApplicationException`, Infrastructure 레이어에서 전파된 `OptimisticLockException`, **`independent/outbox` 모듈의 Infrastructure 레이어 내 `exception` 패키지 (`independent/outbox/infrastructure/exception/` - Rule 80 참조, Rule 141 미적용)에서 전파된 Outbox 관련 예외**를 모두 catch한다. **이 핸들러는 catch한 예외(및 그 안의 `errorCode`)의 타입이나 `code` 값, 또는 표준 예외 타입(`OptimisticLockException`, `MethodArgumentNotValidException` 등)을 기반으로, 자체적인 매핑 로직(예: `when` 문, Map 등)을 통해 적절한 `HttpStatus`를 결정하고, 이를 사용하여 Rule 50에 따라 `ResponseEntity`를 생성하는 책임을 진다.** 또한, 예외 객체가 가진 `errorCode` 정보를 사용하여 RFC 9457 `ProblemDetail` 형식의 응답 본문을 구성한다. GlobalExceptionHandler에서 `errorCode` 정보에 접근하기 위해 Common 모듈의 `DomainException` (`domains/common/domain/exception/DomainException.kt` - Rule 141 준수) 및 `ApplicationException` (`domains/common/application/exception/ApplicationException.kt` - Rule 141 준수) 클래스에 `abstract val errorCode: ErrorCode` 추상 프로퍼티를 유지한다. GlobalExceptionHandler는 특히 Rule 68에서 정의된 Validation 관련 `DomainException` 하위 타입을 식별하여, Rule 48의 `invalid-params` 필드 등 유효성 검사 실패에 특화된 응답 상세를 포함할 수 있다. **Outbox 예외의 경우, 해당 예외 타입 및 포함된 에러 정보를 기반으로 `GlobalExceptionHandler` 내 매핑 로직을 통해 적절한 `HttpStatus` 및 `ProblemDetail`을 구성한다.**
74. **비동기 Command 예외 처리**: 비동기 Command Handler는 예외 발생 시 `jobId`와 함께 예외 정보 및 `ErrorCode`를 별도의 `JobError` 엔티티에 저장하고, 상태 조회 API를 통해 클라이언트가 확인할 수 있도록 한다.
75. **재시도 로직 포함**: Application 레이어 (`domains/{domain}/application/` - Rule 141 준수)는 일시적 오류(예: 외부 API 호출 실패)에 대해 최대 3회 재시도 로직을 포함하며, 재시도 실패 시 해당 도메인 모듈의 `ApplicationException` sealed class (기술적 오류 관련 data class - `domains/{domain}/application/exception/` - Rule 141 준수)를 발생시킨다. (Resilience4j `@Retry` 사용 권장)
76. **Resilience4j 회로 차단기**: 외부 시스템 호출은 Resilience4j를 사용하여 회로 차단기 패턴을 적용하며, 기본 타임아웃은 5초로 설정한다. 회로 차단기 설정은 `@CircuitBreaker` 어노테이션을 사용하고, 폴백 메서드를 정의하여 장애 시 기본 응답을 반환한다.
77. **Resilience4j 재시도 설정**: Resilience4j의 `@Retry` 어노테이션을 사용하여 일시적 오류에 대해 최대 3회 재시도하며, 재시도 간격은 지수 백오프(exponential backoff) 전략을 따른다.

### 트랜잭셔널 아웃박스 패턴 규칙

78. **트랜잭셔널 아웃박스 패턴 도입**: 애플리케이션 상태 변경과 이벤트 발행의 원자성 보장을 위해 트랜잭셔널 아웃박스 패턴을 사용한다. Domain Aggregate에서 발생한 이벤트는 Repository를 통해 저장 시 Database의 Outbox 테이블에 함께 기록된다.
79. **독립적인 Outbox 모듈 정의**: Outbox 패턴 구현 및 관리를 위해 프로젝트 최상위 레벨의 `independent/` 폴더 내에 별도의 독립 모듈(예: `independent/outbox/` - Rule 80 참조, Rule 141 미적용)을 정의한다.
80. **Outbox 모듈 책임 및 독립성**: Outbox 모듈 (`independent/outbox/`)은 도메인 이벤트를 담고 있는 메시지(`OutboxMessage`)의 데이터베이스 저장, 주기적인 조회, Kafka 전송 처리, 관련 상태 관리 및 오류 처리를 담당한다. 이 모듈은 자체적인 레이어 구조(application, infrastructure 등)를 가지며, **Rule 141의 패키지/디렉토리 구조 규칙을 따르지 않고**, **프로젝트 내의 어떤 특정 도메인 모듈 또는 `domains/common` 모듈에도 의존하지 않아 (규칙 9 참조)** 다른 프로젝트로의 **이식성(portability)**을 가집니다. Outbox 모듈은 자체적인 예외(`OutboxException`) 및 에러 코드 체계를 각각 **해당 모듈의 Infrastructure 레이어 내 `exception` 및 `error` 패키지** (`independent/outbox/infrastructure/exception/`, `independent/outbox/infrastructure/error/` - Rule 67, 68, 90 참조) 내에 정의하고 사용한다.
81. **Outbox 메시지 저장 Port (인터페이스) 및 메시지 구조 정의**: Outbox 모듈의 Application 레이어 내 Port 패키지 (`independent/outbox/application/port/` - Rule 80 참조, Rule 141 미적용)에 Outbox에 저장될 메시지 목록 저장을 위한 기술 독립적인 Port(인터페이스)를 정의한다 (예: `OutboxMessageRepository.kt`). 이 인터페이스는 `List<OutboxMessage>`를 받아 저장하는 메서드를 제공해야 한다. 저장될 메시지의 구조는 `OutboxMessage` data class로 정의하며, 이는 `independent/outbox/application/port/model/OutboxMessage.kt` (Rule 80 참조, Rule 141 미적용)에 위치한다. `OutboxMessage`는 Kafka 전송에 필요한 모든 정보(raw payload bytes `ByteArray`, target topic name `String`, headers `Map<String, String>` - including `correlationId`, `aggregateType`, `aggregateId` (String), event type name 등)를 포함해야 하며, 프로젝트의 `DomainEvent` 인터페이스나 특정 도메인의 구체적인 `DomainEvent` 클래스 또는 Avro 스키마/생성 클래스에 의존하지 않는다.
82. **Outbox 메시지 저장 Adapter (구현체)**: Outbox 메시지 저장 Port의 구현체는 Outbox 모듈의 Infrastructure 레이어 내 persistence 패키지 (`independent/outbox/infrastructure/persistence/` - Rule 80 참조, Rule 141 미적용)에 위치한다 (예: `JpaOutboxMessageRepository.kt`). 이 구현체는 JPA 등의 기술을 사용하여 `OutboxEventEntity`를 데이터베이스에 저장하는 로직을 포함한다. `OutboxMessage`를 받아 `OutboxEventEntity`로 변환하여 저장한다.
83. **Outbox 이벤트 엔티티**: Outbox 메시지를 저장하기 위한 `OutboxEventEntity`는 Outbox 모듈의 Infrastructure 레이어 내 entity 패키지 (`independent/outbox/infrastructure/entity/OutboxEventEntity.kt` - Rule 80 참조, Rule 141 미적용)에 정의한다. 이 엔티티는 Rule 81의 `OutboxMessage` 구조에 대응하여 최소한 이벤트 payload (바이트 배열 `ByteArray`), 이벤트 타입명(`String`), 타겟 Kafka 토픽명(`String`), 생성 시간, 상태 (PENDING, PROCESSING, SENT, FAILED 등), 관련 정보 (correlationId(`String`), aggregateType(`String`), aggregateId(`String`)), 버전 관리 필드, 재시도 횟수(`retryCount`), 마지막 시도 시간(`lastAttemptTime`) 필드를 포함해야 한다. `aggregateId` 필드는 Domain ID Value Object가 캡슐화한 UUID의 문자열 형태를 저장한다.
84. **리포지토리 구현체의 Outbox 의존성**: 각 도메인의 **Infrastructure 영속성 담당 모듈(예: `infrastructure/persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)** 에 위치한 Repository 구현체(예: `domains/user/infrastructure/persistence/repository/UserRepositoryImpl.kt`)는 독립적인 `independent/outbox` 모듈의 Outbox 메시지 저장 Port (`OutboxMessageRepository` - `independent/outbox/application/port/` - Rule 80 참조, Rule 141 미적용)를 의존성으로 주입받아 사용한다.
85. **Repository 저장 시 이벤트 처리 및 메시지 변환 책임**: Repository 구현체(**`persistence` 모듈** - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)는 Aggregate 저장 시, 해당 Aggregate의 `aggregateRoot.getDomainEvents()` 메서드를 통해 발생한 이벤트 목록을 읽어온다. **이 이벤트 목록을 동일 도메인 모듈의 Infrastructure 메시징 담당 모듈(`messaging` - `domains/{domain}/infrastructure/messaging/` - Rule 141 준수)에 정의된 컴포넌트(예: `OutboxMessageFactory` - `domains/{domain}/infrastructure/messaging/serialization/OutboxMessageFactory.kt` - Rule 141 준수)에 전달하여, 해당 컴포넌트가 각 `DomainEvent` 객체에 대해 다음을 수행하여 `OutboxMessage` 객체를 생성하도록 해야 한다:**
    1.  Domain Event 객체를 Rule 92에 정의된 직렬화 포맷(예: Avro)으로 직렬화하여 raw payload bytes를 생성한다. 이 과정에서 Rule 106에 정의된 해당 도메인의 Infrastructure 메시징 모듈 내 Avro 스키마 및 Rule 109, 111에 따라 생성된 **Avro Java 클래스 (생성 위치: 해당 스키마 모듈 빌드 경로 - Rule 141 준수)를 직접 사용하여** 데이터를 매핑하고, Apache Avro 라이브러리 등을 통해 직렬화를 수행한다. Rule 120에 따라 Common Infrastructure 모듈에 정의된 Envelope 스키마를 포함하여 메시지를 구성한다. (**이 단계는 `messaging` 모듈 컴포넌트의 책임**)
    2.  Rule 98에 따라 타겟 Kafka 토픽명을 결정한다. (**이 단계는 `messaging` 모듈 컴포넌트의 책임**)
    3.  Rule 115, 117, 118, 119에 따라 Envelope 헤더 정보(correlationId, aggregateType, aggregateId 등)를 준비한다. aggregateId는 Aggregate의 Domain ID Value Object의 문자열 표현을 사용한다. (**이 단계는 `messaging` 모듈 컴포넌트의 책임**)
    4.  Rule 81에 정의된 `OutboxMessage` 객체 (`independent/outbox/application/port/model/OutboxMessage.kt` - Rule 80 참조)를 생성한다. (**이 단계는 `messaging` 모듈 컴포넌트의 책임**)
    준비된 `OutboxMessage` 객체 목록을 동일 트랜잭션 내에서 독립적인 `independent/outbox` 모듈의 Outbox 메시지 저장 Port (`outboxMessageRepository.save(listOfOutboxMessages)` - `independent/outbox/application/port/OutboxMessageRepository.kt` - Rule 80 참조)를 호출하여 저장해야 한다. 이벤트 저장이 완료된 후에는 `aggregateRoot.clearDomainEvents()`를 호출하여 Aggregate 내부의 이벤트 목록을 초기화해야 한다.
86. **Outbox 폴링/전송 컴포넌트**: Outbox 모듈의 Application 레이어 (`independent/outbox/application/` - Rule 80 참조, Rule 141 미적용)에 Outbox 테이블에서 이벤트를 주기적으로 조회하고 Kafka로 전송하는 컴포넌트(Outbox 프로세서)를 구현한다 (예: `OutboxPoller.kt`, `OutboxMessageSender.kt`).
87. **Outbox 폴링 동시성 제어**: Outbox 폴러는 여러 인스턴스가 동시에 실행될 수 있으므로, 이벤트를 읽어올 때 데이터베이스 수준의 잠금(`FOR UPDATE SKIP LOCKED` 등)을 사용하여 동일한 이벤트가 여러 번 처리되지 않도록 해야 한다. 이 로직은 `independent/outbox` 모듈 내에서 관리된다 (Rule 80 참조).
88. **Outbox 메시지 전송**: Outbox Message Sender는 Outbox Event Entity (`independent/outbox/infrastructure/entity/OutboxEventEntity.kt` - Rule 80 참조)를 읽어와 OutboxMessage 객체를 재구성한다. Outbox Event Entity에 저장된 raw payload bytes를 그대로 Kafka 메시지 payload로 사용하여, 해당 Outbox Event Entity에 저장된 target Kafka topic으로 전송한다. 이 과정에서 Outbox Message Sender는 특정 도메인 이벤트 타입이나 Avro 스키마, 자동 생성된 Avro Java 클래스를 직접 참조하지 않는다. 필요한 Kafka Serializer 설정은 `independent/outbox` 모듈 내에서 관리된다 (Rule 80 참조). 이벤트 전송 시 Kafka 메시지 헤더에 `correlationId` 및 Aggregate ID 등 Outbox Event Entity에 저장된 정보를 포함한다.
89. **Outbox 이벤트 상태 업데이트**: Outbox 프로세서는 메시지 전송 결과에 따라 Outbox Event Entity (`independent/outbox/infrastructure/entity/OutboxEventEntity.kt` - Rule 80 참조)의 상태를 업데이트한다.
90. **Outbox 프로세서 재시도/실패 처리**: Outbox 프로세서의 메시지 전송 실패는 `independent/outbox` 모듈 내에서 정의된 재시도 정책에 따른다 (Rule 80 참조). `OutboxEventEntity`의 `retryCount` 필드를 증가시키며 지수 백오프(exponential backoff) 등 적절한 간격으로 재시도를 수행한다. 정의된 최대 재시도 횟수를 초과한 이벤트는 DLQ로 보내거나 별도의 실패 테이블/메커니즘으로 관리한다. 실패 처리 시 **`independent/outbox` 모듈의 Infrastructure 레이어 내 `exception` 패키지 (`independent/outbox/infrastructure/exception/` - Rule 80 참조) 내에서 정의된 자체 예외 및 Infrastructure 레이어 내 `error` 패키지 (`independent/outbox/infrastructure/error/` - Rule 80 참조) 내에서 정의된 에러 코드**를 사용한다.

### 불변식 규칙 (Kafka)

91. **메시징 시스템**: 외부 시스템과의 통신을 위한 메시징 시스템으로 Apache Kafka를 사용한다.
92. **데이터 직렬화 포맷**: Kafka 메시지의 데이터 직렬화 포맷으로 Avro를 사용한다. 이 직렬화는 Rule 85에 따라 해당 도메인 모듈의 **Infrastructure 메시징 담당 모듈(예: `infrastructure/messaging` - `domains/{domain}/infrastructure/messaging/` - Rule 141 준수)** 에서 담당한다.
93. **스키마 관리**: Avro 스키마는 Confluent Schema Registry를 통해 중앙 집중식으로 관리하며 버전 호환성을 보장한다.
94. **Kafka 클라이언트**: Spring Kafka (`spring-kafka`) 라이브러리를 사용하여 Kafka와 연동하며, 이 설정 및 사용은 주로 `independent/outbox` 모듈 (Rule 80 참조, Rule 141 미적용) 또는 메시지 소비를 담당하는 모듈 (해당 도메인 `infrastructure/messaging` 등 - Rule 141 준수)에서 이루어진다.
95. **Kafka 사용 범위**: 모든 도메인 이벤트 발행(Aggregate 내 `addDomainEvent`로 추가된 이벤트)은 트랜잭셔널 아웃박스 패턴을 통해 처리된다. Database의 Outbox 테이블에 이벤트가 기록된 후, `independent/outbox` 모듈의 프로세서 (Rule 80 참조)가 이를 Kafka 토픽으로 전송한다. Kafka는 내부 및 외부 시스템 간의 비동기/이벤트 기반 통신 채널로 사용된다.
96. **분산 트랜잭션**: 분산 트랜잭션 처리가 필요한 경우 Saga 패턴을 적용하며, 상태 변경 명령과 보상 트랜잭션을 Kafka 이벤트를 통해 전달한다. Saga 오케스트레이션/코레오그래피는 `independent/outbox` 패턴 (Rule 80 참조)을 통해 메시지를 발행한다.
97. **요청-응답 패턴**: Kafka를 사용한 동기적 요청-응답 패턴은 Kafka Streams 또는 별도 HTTP API 연동을 고려하며, 비동기 이벤트는 `independent/outbox` 모듈 (Rule 80 참조)을 통해 Kafka 토픽으로 발행한다.
98. **Kafka 토픽 이름**: Kafka 토픽 이름은 다음 형식을 따른다: `{environment}.{domain}.{event-type}.{entity}.{version}`. 여기서 `{version}`은 해당 토픽이 다루는 이벤트 스키마의 논리적/주요 변경 버전을 나타낼 수 있으나, Schema Registry를 통한 동일 토픽 내 스키마 진화 (Backward/Forward 호환성)를 우선적으로 고려하며, 호환성이 유지되는 스키마 변경 시에는 토픽 이름의 version을 변경하지 않는다. 토픽명 결정 및 메시지 구성은 Rule 85에 따라 해당 도메인 모듈의 **Infrastructure 메시징 담당 모듈(`messaging` - `domains/{domain}/infrastructure/messaging/` - Rule 141 준수)** 에서 수행된다.
99. **토픽 네이밍 컨벤션 - environment**: `environment`는 운영 환경을 나타낸다 (예: `prod`, `staging`, `dev`).
100. **토픽 네이밍 컨벤션 - domain**: `domain`은 도메인 이름을 나타낸다 (예: `user`, `order`, `payment`).
101. **토픽 네이밍 컨벤션 - event-type**: `event-type`은 이벤트 유형을 나타낸다 (예: `domain-event`, `saga`, `request`, `response`).
102. **토픽 네이밍 컨벤션 - entity**: `entity`는 이벤트가 발생한 주요 엔티티 또는 애그리거트 이름을 나타낸다 (예: `user`, `order`, `transaction`).
103. **토픽 네이밍 컨벤션 - version**: `version`은 스키마 레지스트리의 물리적 `schemaVersion`이 아닌, 해당 토픽의 주요 변경 사항이나 논리적 스키마 버전을 구분하기 위해 사용될 수 있다.
104. **외부 도메인 이벤트 토픽**: 외부 시스템으로 발행되는 도메인 이벤트는 토픽 유형을 `domain-event`로 한정한다.
105. **Kafka 토픽 이름 제약**: Kafka 토픽 이름은 소문자와 하이픈(`-`)만 사용하고, 최대 249자로 제한한다.
106. **Avro 스키마 파일 위치**: Avro 스키마 `.avsc` 파일은 Domain 모델이 아닌 메시징 포맷 정의를 나타내므로, 해당 스키마를 **사용하고 자동 생성된 Java 클래스를 생성하는** 모듈의 Infrastructure 레이어 `src/main/resources/avro/` 디렉터리에 저장한다. 도메인 이벤트 스키마는 해당 도메인 모듈의 **Infrastructure 메시징 담당 모듈(예: `infrastructure/messaging` - `domains/{domain}/infrastructure/messaging/src/main/resources/avro/` - Rule 141 준수)** 에 위치하며, 공통 Envelope 스키마는 `domains/common` 모듈의 **Infrastructure 레이어** (`domains/common/infrastructure/src/main/resources/avro/` - Rule 141 준수)에 위치한다. `domains/common` 모듈에 `infrastructure/` 하위 모듈이 없는 경우, 이를 생성하여 사용한다. `independent/outbox` 모듈은 도메인 이벤트 스키마 파일이나 공통 Envelope 스키마 파일을 **직접 포함하거나 의존하지 않는다** (Rule 80 참조, Rule 141 미적용).
107. **Avro 스키마 파일명**: Avro 스키마 파일명은 `{entity}-{event-type}-{version}.avsc` 형식을 따라야 한다 (예: `user-domain-event-v1.avsc`, `envelope-common-v1.avsc`). `{entity}`는 규칙 102의 entity를 따르고 `{event-type}`은 규칙 101의 event-type을 따른다.
108. **Avro 스키마 관리**: Avro 스키마 `.avsc` 파일을 작성하여 Confluent Schema Registry에 등록하고 관리하며, 스키마 변경 시에는 새로운 `.avsc` 파일을 생성하고 기존 스키마와의 호환성(Backward, Forward, Full)을 보장한다. 동일 토픽 내에서의 스키마 진화를 우선적으로 적용한다.
109. **Avro Java 클래스 생성**: Gradle 플러그인(`com.github.davidmc24.gradle.plugin.avro`)을 사용하여 Avro 스키마로부터 **Java** 클래스를 자동 생성한다. 이 자동 생성은 **스키마 파일이 위치한 모듈(예: 도메인 `infrastructure/messaging`, `domains/common/infrastructure` - Rule 141 준수)** 에서만 발생한다. `independent/outbox` 모듈은 이 자동 생성 **Java** 클래스에 의존하지 않는다 (Rule 80 참조, Rule 141 미적용).
110. **Avro Java 클래스 설정**: Gradle Avro 플러그인 설정 시 `createSetters = false` 및 `fieldVisibility = "PRIVATE"` 옵션을 설정하여 생성되는 **Java** 클래스에 적용한다. (이 옵션들이 Java 코드 생성 시 원하는 스타일을 지원하는지 확인 필요)
111. **자동 생성 Avro Java 클래스 패키지 위치**: 자동 생성된 Avro **Java** 클래스는 해당 모듈(스키마 파일 위치 모듈)의 빌드 경로(예: `build/generated-src/avro/main/java/`)에 생성된다. 자동 생성된 클래스의 최종 패키지 구조는 해당 모듈(**예: 도메인 `infrastructure/messaging` 모듈, `domains/common/infrastructure` 모듈** - Rule 141 준수)의 기본 패키지(예: `com.restaurant.user.infrastructure.messaging`, `com.restaurant.common.infrastructure`) 하위에 스키마 파일에 정의된 **완전한 `namespace` 그대로** 생성되도록 Gradle 설정을 구성해야 한다 (예: 스키마 `namespace`가 `com.restaurant.user.infrastructure.messaging.avro.event`인 경우, 최종 패키지는 `com.restaurant.user.infrastructure.messaging.avro.event`).
111.5. **자동 생성 Avro Java 클래스 사용 범위 제한**: Avro 스키마로부터 자동 생성된 **Java** 클래스는 해당 클래스가 생성된 모듈(예: 도메인 `infrastructure/messaging` 모듈, `domains/common/infrastructure` 모듈 - Rule 141 준수)의 Infrastructure Layer 내에서도 Kafka 메시지 직렬화/역직렬화를 **직접 담당하는 특정 어댑터 컴포넌트**(예: `OutboxMessageFactory`, Kafka Consumer 리스너 구현체 - `domains/{domain}/infrastructure/messaging/serialization/` 등 - Rule 141 준수) **내부에서만 사용되어야 합니다.** 이 컴포넌트 내부에서는 Kotlin Domain 객체(예: `DomainEvent`)를 **자동 생성된 Avro Java 클래스로 직접 매핑**하는 로직을 구현할 수 있습니다. 별도의 중간 Kotlin DTO 계층 사용은 필수가 아닙니다. 단, 자동 생성된 Avro **Java** 클래스는 Domain Layer, Application Layer, 또는 해당 컴포넌트 외부의 다른 Infrastructure 코드로 **절대 노출되거나 직접 참조되어서는 안 됩니다.**
112. **`Envelope` 스키마 파일 위치**: 공통 `Envelope` 스키마 파일은 `domains/common` 모듈의 **Infrastructure 레이어** (`domains/common/infrastructure/src/main/resources/avro/envelope.avsc` - Rule 141 준수) 에 위치한다.
113. **`Envelope` 스키마 구조**: `Envelope` 스키마는 `schemaVersion`, `eventId` (Correlation ID), `timestamp`, `source`, `aggregateType`, `aggregateId` 필드를 포함한다.
114. **`Envelope` 스키마 필드 - schemaVersion**: `Envelope` 스키마의 `schemaVersion` 필드는 Envelope 스키마 자체의 버전 또는 포함된 메시지 스키마의 물리적 버전을 기록한다.
115. **`Envelope` 스키마 필드 - eventId**: `Envelope` 스키마의 `eventId` 필드는 UUID로 고유 식별자를 생성하여 할당하며, 요청 추적(`correlationId`)에 사용된다.
116. **`Envelope` 스키마 필드 - timestamp**: `Envelope` 스키마의 `timestamp` 필드는 이벤트 발생 시각을 **마이크로초** 단위로 기록한다 (Unix epoch microseconds).
117. **`Envelope` 스키마 필드 - source**: `Envelope` 스키마의 `source` 필드는 이벤트 발행 도메인을 기록한다 (예: `user`, `order`).
118. **`Envelope` 스키마 필드 - aggregateType**: `Envelope` 스키마의 `aggregateType` 필드는 이벤트가 발생한 애그리거트의 타입을 기록한다 (예: `User`).
119. **`Envelope` 스키마 필드 - aggregateId**: `Envelope` 스키마의 `aggregateId` 필드는 이벤트가 발생한 애그리거트의 **Domain ID Value Object** (예: UUID의 문자열 형태)를 기록한다.
120. **공통 Envelope 활용**: 공통 `Envelope` 스키마는 모든 도메인 이벤트 메시지에 포함하며, **각 도메인 Infrastructure의 메시징 담당 모듈(`messaging` - `domains/{domain}/infrastructure/messaging/` - Rule 141 준수)에서** Domain Event를 Rule 85에 따라 Avro bytes로 직렬화할 때 Envelope를 사용하여 메시지를 구성한다. `independent/outbox` 모듈은 Envelope 스키마나 해당 스키마로부터 생성된 **Java** 클래스에 의존하지 않고, Rule 81의 `OutboxMessage` 객체에 포함된 raw bytes와 헤더 맵만 처리한다 (Rule 80 참조, Rule 141 미적용).
121. **공통 스키마 변경 관리**: 공통 스키마 변경 시, 모든 의존 모듈(도메인 Infrastructure 등 - Rule 141 준수)에서 호환성 테스트 후 배포한다.
122. **공통 모듈 의존성**: **특정 도메인 모듈(예: `domains/user`)이 `domains/common` 모듈을 사용하고자 할 때는** `dependencies { implementation project(":domains:common") }` 와 같이 의존성을 추가한다. **`domains/common` 모듈 자체는 다른 특정 도메인 모듈이나 `independent/` 모듈에 의존하지 않습니다 (규칙 9 참조).** **독립 모듈(`independent/` 하위 모듈)은 `domains/common` 모듈에 의존하지 않는다 (규칙 4, 9, 80 참조).**
123. **Saga 토픽 네이밍**: Saga 패턴 관련 Kafka 토픽은 `{environment}.{domain}.saga.{entity}.{version}` 형식으로 명명한다.
124. **Request/Response 토픽 네이밍**: Request & Response 패턴 관련 Kafka 요청 토픽은 `{environment}.{domain}.request.{entity}.{version}` 형식으로, 응답 토픽은 `{environment}.{domain}.response.{entity}.{version}` 형식으로 명명한다.
125. **Request/Response Correlation**: Request & Response 패턴 구현 시 요청과 응답 매핑을 위해 `Envelope.eventId` 필드를 `correlationId`로 사용한다.
126. **Kafka 설정**: Spring Kafka 설정 시 `KafkaTemplate`, `ProducerFactory`, `ConsumerFactory` 빈을 `@Configuration`, `@Bean` 을 사용하여 설정하며, 이러한 설정은 주로 메시지 전송/수신을 직접 담당하는 모듈 (예: `independent/outbox` 모듈의 Kafka Sender - Rule 80 참조, Kafka Consumer 리스너를 포함하는 도메인 `infrastructure/messaging` 모듈 - Rule 141 준수)에 위치한다.
127. **Kafka 설정 - Bootstrap Servers**: Kafka 설정 시 `BOOTSTRAP_SERVERS_CONFIG` 설정을 포함한다.
128. **Kafka 설정 - Key Serializer**: Kafka 설정 시 `KEY_SERIALIZER_CLASS_CONFIG` 설정을 포함한다 (예: `org.apache.kafka.common.serialization.StringSerializer`).
129. **Kafka 설정 - Value Serializer**: Kafka 설정 시 `VALUE_SERIALIZER_CLASS_CONFIG` 설정을 포함한다 (예: `io.confluent.kafka.serializers.KafkaAvroSerializer`). 이는 Rule 85에 따라 해당 도메인 모듈의 Infrastructure 메시징 담당 모듈 (`domains/{domain}/infrastructure/messaging/` - Rule 141 준수)에서 Kafka 전송을 위해 필요하며, 메시지 페이로드 자체는 이미 바이트 배열로 직렬화되어 전달된다.
130. **Kafka 설정 - Schema Registry URL**: Kafka 설정 시 `schema.registry.url` 설정을 포함한다.
131. **Schema Registry URL 설정**: 스키마 레지스트리 URL 설정은 `application.yml` 파일에 `spring.kafka.properties.schema.registry.url` 속성으로 설정한다.
132. **Schema Registry 호환성**: Avro 스키마 변경 시 Confluent Schema Registry의 호환성 규칙 (기본: Backward)을 준수한다.
133. **토픽 세분화**: 대규모 트래픽 및 관심사 분리를 고려하여 도메인별/이벤트 유형별로 토픽을 세분화한다.
134. **통합 테스트**: Kafka와 Schema Registry를 포함한 통합 테스트는 Testcontainers를 사용하여 작성한다.
135. **이벤트 타임스탬프**: 이벤트 `timestamp`는 **마이크로초** 단위로 기록하고, 시스템 간 시간 동기화 (NTP 등)를 고려한다.
136. **모니터링**: 스키마 버전 불일치 등 잠재적 오류를 감지하기 위한 모니터링 설정을 구축한다. `independent/outbox` 모듈 자체의 메트릭(저장된 메시지 수, 전송 실패 수 등)도 모니터링 대상이다 (Rule 80 참조).

### 데이터베이스 접근 규칙 (리포지토리 Naming 포함)

137. **리포지토리 인터페이스 정의**: Domain 레이어의 리포지토리 (`domains/{domain}/domain/repository/` - Rule 141 준수)는 순수한 Port 역할을 수행하며, 특정 영속화 기술에 종속되지 않는 인터페이스로 정의한다 (예: `UserRepository`). 이 인터페이스는 Domain 모델 객체 (Immutable Aggregate의 경우 업데이트된 상태를 반영하는 *새로운 인스턴스*)를 반환/사용해야 한다.
138. **리포지토리 구현체 위치**: Domain 레이어 (`domains/{domain}/domain/` - Rule 141 준수)에서 정의된 리포지토리 인터페이스의 실제 구현체는 해당 도메인 모듈의 **Infrastructure 영속성 담당 모듈(예: `infrastructure/persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)의 `repository` 패키지**에 위치한다.
139. **리포지토리 구현체 네이밍 및 책임**: **Infrastructure 영속성 담당 모듈(`persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)의 `repository` 패키지**에 위치한 리포지토리 구현체는 사용하는 기술 스택을 명시하는 접미사를 붙인다 (예: Spring Data JPA 사용 시 `SpringDataJpaUserRepository`, 커스텀 구현 시 `UserRepositoryImpl`). `SpringDataJpaUserRepository`는 Spring Data JPA가 요구하는 인터페이스를 상속받고, `UserRepositoryImpl`은 Domain 레이어의 `UserRepository` 인터페이스를 구현하며 `SpringDataJpaUserRepository`와 독립적인 `independent/outbox` 모듈의 Outbox 메시지 저장 Port (`OutboxMessageRepository` - Rule 80 참조), **그리고 동일 도메인 모듈 Infrastructure 메시징 담당 모듈(`messaging` - `domains/{domain}/infrastructure/messaging/` - Rule 141 준수)의 이벤트 처리 컴포넌트(예: `OutboxMessageFactory` - Rule 85 참조)** 를 주입받아 사용한다. Repository 구현체는 JPA Entity 저장 시 Rule 85에 따라 Aggregate에서 발생한 Domain Events를 읽어와 **이벤트 처리 컴포넌트(`OutboxMessageFactory`)에 전달하여, 자동 생성된 Avro Java 클래스 (Rule 109, 111, 111.5 참조)를 사용하여 Kafka 메시지 형식(Avro bytes, topic, headers 등)으로 변환된 `OutboxMessage` 객체 목록을 생성하고**, 이를 독립적인 `independent/outbox` 모듈의 `OutboxMessageRepository` (Rule 80 참조)를 통해 저장한다. JPA Entity 저장 후 반환된 Entity를 Domain Aggregate (Immutable Aggregate의 경우 *새로운 인스턴스*)로 변환하여 Domain Port의 `save` 메서드 반환 타입 요구사항을 만족해야 한다. 또한 Domain 객체 (Domain ID Value Object 포함)와 JPA Entity 간의 변환을 담당하며, **Rule 85에 명시된 대로 이벤트 처리 위임 및 Outbox 저장 로직**을 포함한다. 조회 시에는 Domain ID (Value Object가 캡슐화한 UUID)를 사용하여 JPA Entity의 `domainId` 필드를 조회하는 쿼리 메서드를 정의하고 사용한다.
140. **JPA 리포지토리 사용**: Spring Data JPA 등의 기술별 리포지토리 인터페이스는 **Infrastructure 영속성 담당 모듈(`persistence` - `domains/{domain}/infrastructure/persistence/` - Rule 141 준수)의 `repository` 패키지**에서 정의하고 사용한다 (예: `interface SpringDataJpaUserRepository : JpaRepository<UserEntity, Long>`). Domain 레이어는 이 기술별 인터페이스나 구현체를 직접 참조하지 않는다. Repository 구현체는 이 기술별 인터포이스를 사용하여 실제 DB 접근을 수행한다.

**141. 필수 패키지 및 디렉토리 구조 규칙 (도메인 모듈)**
*   **141.1 (적용 범위 및 절대적 지위):** 이 규칙은 `domains/` 디렉토리 아래에 위치하는 모든 모듈 (`domains/common` 포함)에 **필수적으로 적용**되며, 해당 모듈 내 모든 소스 코드, 리소스 파일, 테스트 코드는 이 규칙에 정의된 레이어 및 패키지 구조를 **예외 없이 따라야 합니다.** 본 문서의 다른 규칙에서 특정 요소의 위치를 언급할 때, 해당 위치는 **별도의 명시적인 구조가 정의되지 않는 한** 항상 이 규칙 141에 정의된 레이어 구조 내의 하위 경로를 의미합니다. `independent/` 디렉토리 아래의 모듈에는 이 규칙이 적용되지 않으며 (Rule 80 참조), 해당 모듈은 자체적인 구조 규칙을 정의합니다.
*   **141.2 (최상위 패키지):** 각 도메인 모듈의 최상위 Kotlin/Java 소스 패키지는 `com.restaurant.{domain-name}` 형식을 따라야 합니다 (예: `com.restaurant.user`, `com.restaurant.common`).
*   **141.3 (레이어 패키지):** Rule 141.1에 따라, 각 아키텍처 레이어(Presentation, Application, Domain, Infrastructure 및 Infrastructure 하위 모듈)에 속하는 모든 코드는 반드시 해당 레이어 이름을 딴 하위 패키지 내에 위치해야 합니다. 형식은 `com.restaurant.{domain-name}.{layer-name}` 입니다 (예: `com.restaurant.user.domain`, `com.restaurant.common.presentation`, `com.restaurant.user.infrastructure.persistence`).
*   **141.4 (물리적 경로 일치):** 위 141.3에서 정의된 패키지 구조는 `src/main/kotlin/` (또는 `src/main/java/`) 아래의 물리적 디렉토리 구조와 **정확히 일치**해야 합니다. 예를 들어, `com.restaurant.user.domain` 패키지의 코드는 반드시 `domains/user/domain/src/main/kotlin/com/restaurant/user/domain/` 디렉토리 아래에 위치해야 합니다.
*   **141.5 (레이어 내 표준 하위 패키지):** 각 레이어 패키지 내에서는 코드의 종류에 따라 다음과 같은 표준 하위 패키지 사용을 **필수로 준수**해야 합니다. (디렉토리 구조 예시 참조)
    *   `domain` (`domains/{domain}/domain/` - Rule 141 준수): `aggregate`, `entity` (Domain Entity), `vo`, `event`, `repository` (Port), `error` (Rule 67 준수), `exception` (Rule 68 준수)
    *   `application` (`domains/{domain}/application/` - Rule 141 준수): `dto` (`command`, `query`), `usecase` (Handler), `port` (Inbound), `error` (Rule 67 준수), `exception` (Rule 68 준수)
    *   `presentation` (`domains/{domain}/presentation/` - Rule 141 준수): `api`, `dto` (`request`, `response`), `extensions` (Rule 5 준수 - 버전별 하위 구조), `config`, `filter`
    *   `infrastructure` (`domains/{domain}/infrastructure/` - Rule 141 준수): `persistence` (하위: `entity` (JPA Entity), `repository` (Adapter), `extensions` (Rule 24 준수), `config`), `messaging` (하위: `serialization` (Rule 85 준수), `consumer`, `config`, `avro` (리소스 - Rule 106 준수) 등), `error`, `exception`
*   **141.6 (패키지명 일관성):** 모든 패키지 명명은 소문자를 사용하고, 여러 단어 조합 시에는 일반적으로 붙여씁니다 (예: `userdomain` 대신 `user.domain`).

### 디렉터리 구조 (수정 반영)

**다음 디렉터리 구조는 규칙 2, 4, 5, 7, 8, 19, 24, 32, 33, 34, 51, 58, 59, 60, 61, 67, 68, 70, 73, 80, 81, 82, 83, 84, 85, 86, 90, 92, 98, 106, 109, 111, 111.5, 112, 120, 137, 138, 139, 140, 141 등에 의해 정의된 **필수적인 계층 구조 및 패키지/디렉토리 규칙을 구체적으로 보여주는 예시**입니다. `domains/` 아래의 모든 모듈은 규칙 141에 명시된 구조적 패턴을 준수해야 합니다.** 디렉터리 구조는 레이어별/도메인별 분리 구조를 유지하며, 프로젝트 최상위 레벨에 **독립 모듈 그룹핑 폴더(`independent/` - 다른 프로젝트 재사용성/이식성 고려)** 를 추가하고 그 안에 `outbox/` 모듈을 배치합니다. **Infrastructure 레이어는 `persistence`와 `messaging` 하위 모듈로 분리될 수 있습니다.** Avro 스키마 파일 및 자동 생성된 **Java** 클래스의 위치 및 패키지 구조, Repository 구현체 네이밍 규칙, 공통 모듈의 DomainEvent/AggregateRoot/ErrorCode/Exception 위치 (Rule 141 준수), 도메인 모듈 내 이벤트 그룹화 파일 위치, Domain ID Value Object 위치, Domain/Application ErrorCode 및 Exception 위치를 반영합니다. 도메인 Entity 중 기술적 상세를 포함하지 않는 모델(Rule 11의 정의에 따라 식별자를 가진 객체)은 `domain/{domain}/entity` 패키지에 위치하고, Value Object(Rule 11의 정의에 따라 식별자 없는 객체)는 `domain/{domain}/vo` 패키지에 위치합니다. **`infrastructure/messaging` 모듈 내 수동 작성 Kotlin DTO (예: `avro/dto/UserEventDtos.kt`)는 규칙 111.5 변경에 따라 선택 사항이며, 필요 없을 경우 제거될 수 있습니다.**

**독립 모듈인 `independent/outbox`는 프로젝트 내의 어떤 특정 도메인 모듈 또는 `domains/common` 모듈에도 의존하지 않으며, Rule 141의 구조를 따르지 않습니다 (Rule 9, 80 참조).** `independent/outbox` 내에서는 `OutboxMessage` 구조체, 자체적인 Persistence/Kafka 관련 로직 및 엔티티/설정, 그리고 자체적인 예외 및 에러 코드 체계를 각각 **해당 모듈의 Infrastructure 레이어 내 `exception` 및 `error` 패키지 내에** 정의합니다. Domain 모듈의 **Infrastructure 영속성 담당 모듈(`persistence`)** 은 `independent/outbox`의 Port를 의존하며, Domain Event를 **동일 도메인 모듈 Infrastructure 메시징 담당 모듈(`messaging`)의 컴포넌트에 전달하여, 자동 생성된 Avro Java 클래스 (Rule 109, 111, 111.5 참조)를 사용하여 Kafka 메시지 형식(Avro bytes, topic, headers 등)으로 변환된 `OutboxMessage` 객체를 생성하고** 이를 `OutboxMessageRepository`에 전달합니다. Outbox 모듈은 저장된 raw bytes와 메타데이터를 그대로 Kafka로 전송합니다.

**Presentation 레이어의 DTO 및 확장 함수 폴더 구조는 규칙 5, 7, 8, 58, 59에 따라 명시된 구조를 필수로 준수합니다.**

```
/
├── independent/ # 독립 모듈 그룹핑 폴더 (다른 프로젝트 재사용성/이식성 고려) (rule 4, 79, 80, 141.1 미적용)
│ └── outbox/ # Outbox 독립 모듈 - NO dependencies on domains/* or common/* or other independent/* (rule 9, 80, 122, 141.1 미적용) # Package: com.restaurant.outbox (예시)
│ ├── src/
│ │ ├── main/
│ │ │ ├── kotlin/
│ │ │ │ └── com/
│ │ │ │ └── restaurant/
│ │ │ │ └── outbox/
│ │ │ │ ├── application/ # Application 레이어 (rule 80)
│ │ │ │ │ ├── port/ # Outbox 저장 Port (rule 81)
│ │ │ │ │ │ ├── OutboxMessageRepository.kt # Accepts List<OutboxMessage>
│ │ │ │ │ │ └── model/ # Outbox 메시지 구조 정의 (rule 81)
│ │ │ │ │ │ ├── OutboxMessage.kt # Data class: ByteArray payload, topic, headers etc.
│ │ │ │ │ │ └── OutboxMessageStatus.kt
│ │ │ │ │ └── OutboxPoller.kt # Outbox 폴러 컴포넌트 (rule 86) - 구현 필요
│ │ │ │ └── infrastructure/ # Infrastructure 레이어 (rule 80)
│ │ │ │ ├── entity/ # Outbox 이벤트 엔티티 (rule 83)
│ │ │ │ │ └── OutboxEventEntity.kt # Stores raw payload bytes, topic, headers etc.
│ │ │ │ ├── persistence/ # Outbox 메시지 저장 Adapter (rule 82)
│ │ │ │ │ ├── JpaOutboxMessageRepository.kt # Implements OutboxMessageRepository Port
│ │ │ │ │ └── converter/ # JPA Converter (rule 83)
│ │ │ │ │ └── StringMapConverter.kt
│ │ │ │ ├── kafka/ # Kafka Sender, configuration (rule 86, 88, 126)
│ │ │ │ │ ├── OutboxMessageSender.kt # Sends raw bytes from entity - 구현 필요
│ │ │ │ │ └── config/
│ │ │ │ │ └── KafkaOutboxProducerConfig.kt # Outbox Kafka settings (Producer related)
│ │ │ │ ├── error/ # Outbox 자체 에러 코드 (rule 67, 80, 90)
│ │ │ │ │ └── OutboxErrorCodes.kt # Enum for Outbox errors
│ │ │ │ ├── exception/ # Outbox 자체 예외 (rule 68, 80, 90)
│ │ │ │ │ └── OutboxException.kt # Base exception for Outbox failures
│ │ │ │ └── resources/ # Outbox 관련 리소스
│ │ │ │ │ └── avro/ # Outbox 자체 Avro 스키마 원본 파일 (rule 106) - NOT domain event schemas
│ │ │ │ │ └── outbox-internal-v1.avsc # Example internal schema if needed
│ │ │ │ └── test/
│ └── build.gradle.kts # Depends only on standard libraries/frameworks (JPA, Kafka clients etc.)
├── domains/ # 도메인별 모듈 그룹 (rule 3, 141 필수 적용)
│ ├── common/ # 공통 모듈 (rule 4, 122, 141 필수 적용) # Package: com.restaurant.common (예시) (rule 141.2)
│ │ ├── src/ # NO dependencies on specific domains (e.g., user) or independent/* (rule 4, 9, 122)
│ │ │ ├── main/
│ │ │ │ ├── kotlin/ # Package: com.restaurant.common (예시)
│ │ │ │ │ └── com/
│ │ │ │ │ └── restaurant/
│ │ │ │ │ └── common/
│ │ │ │ │ ├── domain/ # Domain 레이어 (rule 141.3)
│ │ │ │ │ │ ├── aggregate/ # (rule 141.5)
│ │ │ │ │ │ │ └── AggregateRoot.kt # (rule 17, 18)
│ │ │ │ │ │ ├── event/ # (rule 141.5)
│ │ │ │ │ │ │ └── DomainEvent.kt # (rule 32)
│ │ │ │ │ │ ├── error/ # (rule 141.5)
│ │ │ │ │ │ │ ├── ErrorCode.kt # (rule 67)
│ │ │ │ │ │ │ └── CommonSystemErrorCode.kt # (rule 67)
│ │ │ │ │ │ └── exception/ # (rule 141.5)
│ │ │ │ │ │ └── DomainException.kt # Common Base Domain Exception (rule 68, 73)
│ │ │ │ │ ├── application/ # Application 레이어 (rule 141.3)
│ │ │ │ │ │ ├── exception/ # (rule 141.5)
│ │ │ │ │ │ │ └── ApplicationException.kt # Common Base Application Exception (rule 68, 73)
│ │ │ │ │ │ └── # 기타 공통 Application 요소 (예: 공통 Port)
│ │ │ │ │ ├── presentation/ # Presentation 레이어 (rule 141.3)
│ │ │ │ │ │ ├── GlobalExceptionHandler.kt # (rule 51, 52, 73, 141.5)
│ │ │ │ │ │ ├── config/ # (rule 141.5)
│ │ │ │ │ │ │ └── SecurityConfig.kt
│ │ │ │ │ │ ├── filter/ # (rule 141.5)
│ │ │ │ │ │ │ └── CorrelationIdFilter.kt
│ │ │ │ │ │ └── dto/ # (rule 141.5)
│ │ │ │ │ │ └── response/
│ │ │ │ │ │ └── CommandResultResponse.kt # (rule 35, 39)
│ │ │ │ │ └── infrastructure/ # Infrastructure 레이어 (rule 141.3)
│ │ │ │ │ │ ├── persistence/ # (rule 141.5)
│ │ │ │ │ │ │ └── entity/
│ │ │ │ │ │ │ └── BaseEntity.kt
│ │ │ │ │ │ └── # 기타 공통 인프라 컴포넌트
│ │ │ │ │ └── test/
│ │ │ │ └── resources/ # Common Module Resources (rule 141.4)
│ │ │ │ └── avro/ # 공통 Envelope 스키마 (rule 106, 112) - Avro Java classes generated here (Rule 109, 111)
│ │ │ │ │ └── envelope.avsc # Example common envelope schema
│ │ └── build.gradle.kts
│ ├── user/ # 예시 도메인 (rule 3, 141 필수 적용) # Package: com.restaurant.user (예시) (rule 141.2)
│ │ ├── apps/ # 애플리케이션 실행 모듈 (일반적으로 도메인 레이어 구조와 분리)
│ │ │ ├── src/
│ │ │ │ ├── main/
│ │ │ │ │ ├── kotlin/ # Package: com.restaurant.apps.user (예시)
│ │ │ │ │ │ └── com/
│ │ │ │ │ │ └── restaurant/
│ │ │ │ │ │ └── apps/
│ │ │ │ │ │ └── user/
│ │ │ │ │ │ └── UserApplication.kt # Spring Boot Application 진입점
│ │ │ │ │ ├── resources/
│ │ │ │ │ │ └── application.yml # 애플리케이션별 설정
│ │ │ │ │ └── test/
│ │ ├── domain/ # Domain 레이어 모듈 (rule 2, 10, 141 필수 적용) # Package: com.restaurant.user.domain (예시) (rule 141.3)
│ │ │ ├── src/ # Can depend on domains/common (rule 4, 9)
│ │ │ │ ├── main/
│ │ │ │ │ ├── kotlin/ # Package: com.restaurant.user.domain (예시)
│ │ │ │ │ │ └── com/
│ │ │ │ │ │ └── restaurant/
│ │ │ │ │ │ └── user/
│ │ │ │ │ │ └── domain/ # (rule 141.4)
│ │ │ │ │ │ ├── aggregate/ # (rule 141.5)
│ │ │ │ │ │ │ ├── User.kt # AggregateRoot 상속, Immutable, id: UserId 필드 포함
│ │ │ │ │ │ │ ├── UserStatus.kt
│ │ │ │ │ │ │ └── UserType.kt
│ │ │ │ │ │ ├── entity/ # Domain Entity (rule 11, 141.5)
│ │ │ │ │ │ │ └── Address.kt # AddressId 포함
│ │ │ │ │ │ ├── vo/ # Value Object (rule 11, 141.5)
│ │ │ │ │ │ │ ├── AddressId.kt
│ │ │ │ │ │ │ ├── Email.kt # validation throws DomainException (rule 14, 61)
│ │ │ │ │ │ │ ├── Name.kt
│ │ │ │ │ │ │ ├── Password.kt # validation throws DomainException, toString 마스킹 (rule 14, 15, 61)
│ │ │ │ │ │ │ ├── PhoneNumber.kt
│ │ │ │ │ │ │ ├── UserId.kt # UUID 기반 ID VO (rule 10)
│ │ │ │ │ │ │ └── Username.kt
│ │ │ │ │ │ ├── event/ # Domain Event (rule 33, 34, 141.5)
│ │ │ │ │ │ │ └── UserEvent.kt # Aggregate별 sealed class 이벤트 그룹
│ │ │ │ │ │ ├── repository/ # Repository Port (rule 137, 141.5)
│ │ │ │ │ │ │ └── UserRepository.kt
│ │ │ │ │ │ ├── error/ # Domain Error Code (rule 67, 141.5)
│ │ │ │ │ │ │ └── UserDomainErrorCodes.kt # Implements ErrorCode
│ │ │ │ │ │ └── exception/ # Domain Exception (rule 68, 141.5)
│ │ │ │ │ │ └── UserDomainException.kt # Sealed class, Has 'errorCode: ErrorCode'
│ │ │ │ └── test/
│ │ ├── application/ # Application 레이어 모듈 (rule 2, 141 필수 적용) # Package: com.restaurant.user.application (예시) (rule 141.3)
│ │ │ ├── src/ # Can depend on domains/common and independent/*/application/port (rule 4, 9)
│ │ │ │ ├── main/
│ │ │ │ │ ├── kotlin/ # Package: com.restaurant.user.application (예시)
│ │ │ │ │ │ └── com/
│ │ │ │ │ │ └── restaurant/
│ │ │ │ │ │ └── user/
│ │ │ │ │ │ └── application/ # (rule 141.4)
│ │ │ │ │ │ ├── dto/ # (rule 141.5)
│ │ │ │ │ │ │ ├── command/
│ │ │ │ │ │ │ │ └── ...Command.kt
│ │ │ │ │ │ │ └── query/
│ │ │ │ │ │ │ ├── GetUserProfileByIdQuery.kt
│ │ │ │ │ │ │ ├── LoginResult.kt
│ │ │ │ │ │ │ └── UserProfileDto.kt
│ │ │ │ │ │ ├── usecase/ # Command/Query Handler (rule 141.5)
│ │ │ │ │ │ │ └── ...CommandHandler.kt / ...QueryHandler.kt
│ │ │ │ │ │ ├── port/ # Inbound Port (rule 141.5)
│ │ │ │ │ │ │ └── ...UseCase.kt / ...Query.kt
│ │ │ │ │ │ ├── error/ # Application Error Code (rule 67, 141.5)
│ │ │ │ │ │ │ └── UserApplicationErrorCode.kt # Implements ErrorCode
│ │ │ │ │ │ └── exception/ # Application Exception (rule 68, 141.5)
│ │ │ │ │ │ └── UserApplicationException.kt # Sealed class, Has 'errorCode: ErrorCode'
│ │ │ │ │ └── resources/ # Application Resources (rule 141.4)
│ │ │ │ │ └── test/
│ │ ├── infrastructure/ # Infrastructure 계층 (상위 폴더) (rule 2, 141 필수 적용)
│ │ │ ├── persistence/ # 영속성 담당 모듈 (rule 2, 19, 138, 141 필수 적용) # Package: com.restaurant.user.infrastructure.persistence (예시) (rule 141.3)
│ │ │ │ ├── src/ # Can depend on domains/common and independent/*/application/port (rule 4, 9, 84)
│ │ │ │ │ ├── main/
│ │ │ │ │ │ ├── kotlin/ # Package: com.restaurant.user.infrastructure.persistence (예시)
│ │ │ │ │ │ │ └── com/
│ │ │ │ │ │ │ └── restaurant/
│ │ │ │ │ │ │ └── user/
│ │ │ │ │ │ │ └── infrastructure/
│ │ │ │ │ │ │ └── persistence/ # (rule 141.4)
│ │ │ │ │ │ │ ├── entity/ # JPA Entity (rule 19, 141.5)
│ │ │ │ │ │ │ │ ├── UserEntity.kt
│ │ │ │ │ │ │ │ └── AddressEntity.kt
│ │ │ │ │ │ │ ├── repository/ # Repository Adapter (rule 138, 139, 140, 141.5)
│ │ │ │ │ │ │ │ ├── SpringDataJpaUserRepository.kt
│ │ │ │ │ │ │ │ └── UserRepositoryImpl.kt # Depends on OutboxMessageRepository & OutboxMessageFactory
│ │ │ │ │ │ │ └── extensions/ # Entity Conversion Extensions (rule 24, 141.5)
│ │ │ │ │ │ │ ├── UserEntityExtensions.kt
│ │ │ │ │ │ │ └── AddressEntityExtensions.kt
│ │ │ │ │ │ └── resources/ # Persistence Resources (rule 141.4) (예: DB 마이그레이션 스크립트)
│ │ │ │ │ └── test/
│ │ │ │ └── build.gradle.kts # Persistence 모듈 빌드 스크립트
│ │ │ └── messaging/ # 메시징 담당 모듈 (rule 2, 85, 92, 106, 141 필수 적용) # Package: com.restaurant.user.infrastructure.messaging (예시) (rule 141.3)
│ │ │ │ ├── src/ # Can depend on domains/common (rule 4, 9)
│ │ │ │ │ ├── main/
│ │ │ │ │ │ ├── kotlin/ # Package: com.restaurant.user.infrastructure.messaging (예시)
│ │ │ │ │ │ │ └── com/
│ │ │ │ │ │ │ └── restaurant/
│ │ │ │ │ │ │ └── user/
│ │ │ │ │ │ │ └── infrastructure/
│ │ │ │ │ │ │ └── messaging/ # (rule 141.4)
│ │ │ │ │ │ │ ├── serialization/ # Event Serialization/Deserialization (rule 85, 141.5)
│ │ │ │ │ │ │ │ └── OutboxMessageFactory.kt # Uses Avro Java classes (rule 111.5)
│ │ │ │ │ │ │ └── avro/ # (rule 141.5) - Optional Kotlin DTOs for mapping (rule 111.5)
│ │ │ │ │ │ │ └── dto/
│ │ │ │ │ │ │ └── UserEventDtos.kt # 예시 (선택 사항)
│ │ │ │ │ │ └── # 기타 Messaging Components (consumer etc.)
│ │ │ │ │ └── resources/ # Messaging Resources (rule 141.4)
│ │ │ │ │ │ └── avro/ # 해당 도메인 Avro 스키마 원본 파일 (rule 106) - Avro Java classes generated from here (Rule 109, 111)
│ │ │ │ │ │ └── user-domain-event-v1.avsc # Example domain event schema
│ │ │ │ │ └── test/
│ │ │ │ └── build.gradle.kts # Messaging 모듈 빌드 스크립트 (includes avro plugin configuration)
│ │ ├── presentation/ # Presentation 레이어 모듈 (rule 2, 141 필수 적용) # Package: com.restaurant.user.presentation (예시) (rule 141.3)
│ │ │ ├── src/ # Can depend on domains/common (rule 4, 9)
│ │ │ │ ├── main/
│ │ │ │ │ ├── kotlin/ # Package: com.restaurant.user.presentation (예시)
│ │ │ │ │ │ └── com/
│ │ │ │ │ │ └── restaurant/
│ │ │ │ │ │ └── user/
│ │ │ │ │ │ └── presentation/ # (rule 141.4)
│ │ │ │ │ │ ├── v1/ # API Version (rule 57)
│ │ │ │ │ │ │ ├── api/ # Controllers (rule 141.5)
│ │ │ │ │ │ │ │ ├── UserAddressController.kt
│ │ │ │ │ │ │ │ ├── UserController.kt
│ │ │ │ │ │ │ │ └── UserQueryController.kt
│ │ │ │ │ │ │ ├── dto/ # Request/Response DTOs (rule 44, 46, 141.5)
│ │ │ │ │ │ │ │ ├── request/
│ │ │ │ │ │ │ │ │ └── ...RequestV1.kt
│ │ │ │ │ │ │ │ └── response/
│ │ │ │ │ │ │ │ └── ...ResponseV1.kt
│ │ │ │ │ │ │ └── extensions/ # DTO Conversion Extensions (rule 5, 6, 7, 8, 58, 59, 141.5)
│ │ │ │ │ │ │ ├── command/
│ │ │ │ │ │ │ │ └── dto/
│ │ │ │ │ │ │ │ └── request/
│ │ │ │ │ │ │ │ └── UserCommandRequestExtensions.kt # (rule 7, 59)
│ │ │ │ │ │ │ └── query/
│ │ │ │ │ │ │ └── dto/
│ │ │ │ │ │ │ └── response/
│ │ │ │ │ │ │ └── UserQueryResponseExtensions.kt # (rule 7, 59)
│ │ │ │ │ └── resources/ # Presentation Resources (rule 141.4)
│ │ │ │ │ └── test/
│ │ └── build.gradle.kts # User 모듈의 각 하위 모듈(domain, application, infrastructure/persistence 등)이 common 및 independent(port)에 대한 의존성을 가질 수 있음
# 다른 도메인 모듈들 (order 등) 도 user 도메인과 유사한 구조 (rule 3, 141 필수 적용)
└── build.gradle.kts
```

```
### 플러그인 (Plugins) - 최신 스테이블 버전
1. **`org.springframework.boot`**: `3.3.4`  
   - Spring Boot Gradle 플러그인 최신 버전 (Gradle Plugin Portal 및 GitHub 확인).[](https://github.com/spring-projects/spring-boot/releases)
2. **`io.spring.dependency-management`**: `1.1.6`  
   - Spring Dependency Management 플러그인 최신 버전 (Gradle Plugin Portal 확인).
3. **`org.jetbrains.kotlin.jvm`**: `2.0.20`  
   - Kotlin JVM 플러그인 최신 버전 (JetBrains GitHub 및 Maven Central 확인).
4. **`org.jetbrains.kotlin.plugin.spring`**: `2.0.20`  
   - Kotlin Spring 플러그인, Kotlin 버전과 동기화 (JetBrains 공식 문서 확인).
5. **`org.jetbrains.kotlin.plugin.jpa`**: `2.0.20`  
   - Kotlin JPA 플러그인, Kotlin 버전과 동기화 (JetBrains 공식 문서 확인).
6. **`org.jetbrains.kotlin.plugin.allopen`**: `2.0.20`  
   - Kotlin AllOpen 플러그인, Kotlin 버전과 동기화 (JetBrains 공식 문서 확인).
7. **`org.jlleitschuh.gradle.ktlint`**: `12.1.1`  
   - ktlint Gradle 플러그인 최신 버전 (GitHub 릴리스 확인).[](https://github.com/JLLeitschuh/ktlint-gradle)
8. **`com.github.davidmc24.gradle.plugin.avro`**: `1.9.1`  
   - Avro Gradle 플러그인 최신 버전 (GitHub 릴리스 확인).[](https://github.com/davidmc24/gradle-avro-plugin)

### 외부 라이브러리 의존성 - 최신 스테이블 버전
Spring Boot 3.3.4의 BOM(`spring-boot-dependencies`)을 기준으로 관리되는 의존성은 해당 BOM에서 제공하는 버전을 사용합니다. Spring Boot BOM 외의 의존성은 Maven Central 또는 공식 문서에서 최신 버전을 확인했습니다.

1. **`org.jetbrains.kotlin:kotlin-stdlib`**: `2.0.20` (Spring Boot BOM: `2.0.20`)
2. **`org.jetbrains.kotlin:kotlin-reflect`**: `2.0.20` (Spring Boot BOM: `2.0.20`)
3. **`org.jetbrains.kotlin:kotlin-stdlib-common`**: `2.0.20` (Kotlin 버전과 동기화)
4. **`io.github.microutils:kotlin-logging-jvm`**: `3.0.5` (Maven Central 확인)
5. **`org.jetbrains.kotlinx:kotlinx-serialization-core`**: `1.7.3` (Kotlinx GitHub 확인)
6. **`org.springframework.boot:spring-boot-starter`**: `3.3.4` (Spring Boot BOM)
7. **`org.springframework.boot:spring-boot-starter-web`**: `3.3.4` (Spring Boot BOM)
8. **`org.springframework.boot:spring-boot-starter-data-jpa`**: `3.3.4` (Spring Boot BOM)
9. **`org.springframework.boot:spring-boot-starter-validation`**: `3.3.4` (Spring Boot BOM)
10. **`org.springframework.boot:spring-boot-starter-actuator`**: `3.3.4` (Spring Boot BOM)
11. **`org.springframework.boot:spring-boot-starter-security`**: `3.3.4` (Spring Boot BOM)
12. **`org.springframework.boot:spring-boot-starter-aop`**: `3.3.4` (Spring Boot BOM)
13. **`org.springframework.boot:spring-boot-starter-hateoas`**: `3.3.4` (Spring Boot BOM)
14. **`org.springframework:spring-context`**: `6.1.14` (Spring Boot BOM)
15. **`org.springframework:spring-tx`**: `6.1.14` (Spring Boot BOM)
16. **`jakarta.persistence:jakarta.persistence-api`**: `3.1.0` (Spring Boot BOM)
17. **`jakarta.validation:jakarta.validation-api`**: `3.0.2` (Spring Boot BOM)
18. **`com.h2database:h2`**: `2.3.230` (Spring Boot BOM)
19. **`org.postgresql:postgresql`**: `42.7.4` (Spring Boot BOM)
20. **`com.zaxxer:HikariCP`**: `5.1.0` (Spring Boot BOM)
21. **`org.mapstruct:mapstruct`**: `1.6.2` (Spring Boot BOM)
22. **`org.mapstruct:mapstruct-processor`**: `1.6.2` (Spring Boot BOM)
23. **`org.apache.avro:avro`**: `1.12.0` (Spring Boot BOM)
24. **`org.springframework.kafka:spring-kafka`**: `3.2.4` (Spring Boot BOM)
25. **`org.apache.kafka:kafka-clients`**: `3.8.0` (Spring Boot BOM)
26. **`io.confluent:kafka-avro-serializer`**: `7.6.3` (Confluent Maven Repository 확인)
27. **`com.fasterxml.jackson.module:jackson-module-kotlin`**: `2.17.2` (Spring Boot BOM)
28. **`com.fasterxml.jackson.datatype:jackson-datatype-jsr310`**: `2.17.2` (Spring Boot BOM)
29. **`com.fasterxml.jackson.core:jackson-databind`**: `2.17.2` (Spring Boot BOM)
30. **`org.springdoc:springdoc-openapi-starter-webmvc-ui`**: `2.6.0` (Springdoc GitHub 확인)
31. **`io.github.resilience4j:resilience4j-spring-boot3`**: `2.2.0` (Resilience4j GitHub 확인)
32. **`org.slf4j:slf4j-api`**: `2.0.16` (Spring Boot BOM)
33. **`org.springframework.boot:spring-boot-starter-test`**: `3.3.4` (Spring Boot BOM)
34. **`io.kotest:kotest-runner-junit5`**: `5.9.1` (Kotest GitHub 확인)
35. **`io.kotest:kotest-assertions-core`**: `5.9.1` (Kotest GitHub 확인)
36. **`io.mockk:mockk`**: `1.13.13` (MockK GitHub 확인)
37. **`org.mockito.kotlin:mockito-kotlin`**: `5.4.0` (Mockito Kotlin GitHub 확인)
38. **`org.assertj:assertj-core`**: `3.26.3` (Spring Boot BOM)
39. **`org.jetbrains.kotlin:kotlin-test-junit5`**: `2.0.20` (Kotlin 버전과 동기화)
40. **`org.testcontainers:postgresql`**: `1.20.2` (Testcontainers GitHub 확인)
41. **`org.testcontainers:kafka`**: `1.20.2` (Testcontainers GitHub 확인)
42. **`org.testcontainers:junit-jupiter`**: `1.20.2` (Testcontainers GitHub 확인)
```