package com.restaurant.outbox.application.handler

import com.restaurant.outbox.application.usecase.ProcessOutboxEventsUseCase
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Outbox 폴링/전송 컴포넌트
 * Rule 86: Outbox 모듈의 Application 레이어에 위치하는 폴링 컴포넌트
 * Rule 87: 동시성 제어를 위한 데이터베이스 수준 잠금 사용
 */
@Component
@ConditionalOnProperty(
    name = ["outbox.polling.enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
class OutboxPoller(
    private val processOutboxEventsUseCase: ProcessOutboxEventsUseCase,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val batchSize = 100
    private val maxRetries = 3

    /**
     * 대기 중인 메시지를 주기적으로 폴링하여 처리합니다.
     * Rule 86: 주기적인 조회 및 메시지 브로커 전송 처리
     */
    @Scheduled(fixedRate = 1000) // 1초마다 실행
    fun pollAndProcessMessages() {
        try {
            val processedCount = processOutboxEventsUseCase.processPendingMessages(batchSize)
            if (processedCount > 0) {
                logger.debug("Processed {} pending messages", processedCount)
            }
        } catch (e: Exception) {
            logger.error("Error in outbox polling process", e)
        }
    }

    /**
     * 실패한 메시지를 주기적으로 재시도합니다.
     * Rule 90: 재시도 정책에 따른 실패 메시지 처리
     */
    @Scheduled(fixedRate = 300000) // 5분마다 실행
    fun retryFailedMessages() {
        try {
            val retryCount = processOutboxEventsUseCase.retryFailedMessages(maxRetries, batchSize)
            if (retryCount > 0) {
                logger.debug("Marked {} failed messages for retry", retryCount)
            }
        } catch (e: Exception) {
            logger.error("Error in failed message retry process", e)
        }
    }
}
