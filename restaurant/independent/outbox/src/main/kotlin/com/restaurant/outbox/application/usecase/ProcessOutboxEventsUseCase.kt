package com.restaurant.outbox.application.usecase

import com.restaurant.outbox.application.dto.OutboxMessage

/**
 * Outbox 이벤트 처리를 위한 Use Case 인터페이스
 * Rule 80: 독립 모듈의 Application Layer Use Case 인터페이스
 */
interface ProcessOutboxEventsUseCase {
    /**
     * 단일 Outbox 메시지를 처리합니다.
     * @param outboxMessage 처리할 Outbox 메시지
     */
    fun process(outboxMessage: OutboxMessage)

    /**
     * 대기 중인 Outbox 메시지들을 배치로 처리합니다.
     * @param batchSize 처리할 최대 메시지 수
     * @return 처리된 메시지 수
     */
    fun processPendingMessages(batchSize: Int = 100): Int

    /**
     * 실패한 Outbox 메시지들을 재시도합니다.
     * @param maxRetries 최대 재시도 횟수
     * @param batchSize 처리할 최대 메시지 수
     * @return 재시도 대상으로 표시된 메시지 수
     */
    fun retryFailedMessages(
        maxRetries: Int = 3,
        batchSize: Int = 100,
    ): Int
}
