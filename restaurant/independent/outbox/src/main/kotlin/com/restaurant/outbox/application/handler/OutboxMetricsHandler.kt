package com.restaurant.outbox.application.handler

import com.restaurant.outbox.application.dto.OutboxMessageRepository
import com.restaurant.outbox.application.dto.model.OutboxMessageStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Outbox 메트릭 및 모니터링 컴포넌트
 * Rule VII.2.23: 모니터링 설정 구축
 * Rule 80: 독립 모듈의 Application 레이어에 위치
 */
@Component
class OutboxMetricsHandler(
    private val outboxMessageRepository: OutboxMessageRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Outbox 메시지 상태별 통계를 주기적으로 로깅합니다.
     * Rule VII.2.23: 저장된 메시지 수, 전송 실패 수 등 메트릭 모니터링
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    fun logOutboxMetrics() {
        try {
            val pendingCount = outboxMessageRepository.countByStatus(OutboxMessageStatus.PENDING)
            val processingCount = outboxMessageRepository.countByStatus(OutboxMessageStatus.PROCESSING)
            val sentCount = outboxMessageRepository.countByStatus(OutboxMessageStatus.SENT)
            val failedCount = outboxMessageRepository.countByStatus(OutboxMessageStatus.FAILED)
            val deadLetteredCount = outboxMessageRepository.countByStatus(OutboxMessageStatus.DEAD_LETTERED)

            logger.info(
                "Outbox Metrics - PENDING: {}, PROCESSING: {}, SENT: {}, FAILED: {}, DEAD_LETTERED: {}",
                pendingCount,
                processingCount,
                sentCount,
                failedCount,
                deadLetteredCount,
            )

            // 경고 조건 체크
            if (failedCount > 100) {
                logger.warn("High number of failed messages detected: {}", failedCount)
            }

            if (deadLetteredCount > 0) {
                logger.warn("Dead lettered messages detected: {}", deadLetteredCount)
            }

            if (pendingCount > 1000) {
                logger.warn("High number of pending messages detected: {}", pendingCount)
            }
        } catch (e: Exception) {
            logger.error("Failed to collect outbox metrics", e)
        }
    }

    /**
     * 오래된 성공 메시지 정리를 위한 통계 로깅
     * 운영 환경에서 정리 작업 계획 수립을 위한 정보 제공
     */
    @Scheduled(fixedRate = 3600000) // 1시간마다 실행
    fun logCleanupCandidates() {
        try {
            val sentCount = outboxMessageRepository.countByStatus(OutboxMessageStatus.SENT)
            logger.info("Cleanup candidates - SENT messages: {}", sentCount)

            if (sentCount > 10000) {
                logger.info("Consider implementing cleanup job for sent messages")
            }
        } catch (e: Exception) {
            logger.error("Failed to collect cleanup metrics", e)
        }
    }
}
