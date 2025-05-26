package com.restaurant.outbox.application.dto

/** Outbox 메시지 상태 enum */
enum class OutboxMessageStatus {
    /**
     * 처리 대기 중인 메시지
     */
    PENDING,

    /**
     * 처리 중인 메시지
     */
    PROCESSING,

    /**
     * 성공적으로 전송된 메시지
     */
    SENT,

    /**
     * 전송 실패한 메시지
     */
    FAILED,

    /**
     * 최대 재시도 횟수를 초과하여 더 이상 처리하지 않을 메시지
     */
    DEAD_LETTERED,

    /**
     * 더 이상 처리하지 않을 메시지
     */
    DISCARDED,
}
