package com.restaurant.common.domain.event

import java.time.LocalDateTime

/**
 * 모든 도메인 이벤트가 구현해야 하는 공통 인터페이스.
 */
interface DomainEvent {
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
