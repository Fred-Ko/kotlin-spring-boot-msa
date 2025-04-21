package com.restaurant.shared.outbox.application.event

import java.time.LocalDateTime

/**
 * Outbox 모듈 내에서 처리될 도메인 이벤트를 나타내는 인터페이스.
 * domains/common 의존성을 제거하기 위해 정의됨.
 * 실제 이벤트 페이로드는 OutboxMessageEntity 등에 직렬화된 형태로 저장되므로,
 * 이 인터페이스는 주로 이벤트의 메타데이터를 정의한다.
 */
interface OutboxDomainEvent {
    /**
     * 이벤트가 발생한 Aggregate의 루트 엔티티 ID (UUID의 문자열 표현).
     */
    val aggregateId: String

    /**
     * 이벤트가 발생한 Aggregate의 타입 이름.
     */
    val aggregateType: String // 예: "User", "Order"

    /**
     * 이벤트 발생 시각.
     */
    val occurredAt: LocalDateTime

    /**
     * 이벤트 고유 ID.
     */
    val eventId: String
}
