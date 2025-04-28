package com.restaurant.outbox.port

import com.restaurant.outbox.port.model.OutboxMessage
import com.restaurant.outbox.port.model.OutboxMessageStatus
import java.util.UUID

/**
 * Outbox 메시지 저장소에 대한 포트 인터페이스.
 * 이 인터페이스는 Outbox 메시지의 저장, 조회, 상태 업데이트 등의 작업을 정의합니다.
 */
interface OutboxMessageRepository {
    /**
     * 단일 Outbox 메시지를 저장합니다.
     * 이 메서드는 원자적으로 실행되어야 합니다.
     *
     * @param message 저장할 메시지
     * @return 저장된 메시지
     */
    fun save(message: OutboxMessage): OutboxMessage

    /**
     * 여러 Outbox 메시지를 저장합니다.
     * 이 메서드는 원자적으로 실행되어야 합니다 - 모든 메시지가 저장되거나 아무것도 저장되지 않아야 합니다.
     *
     * @param messages 저장할 메시지 목록
     * @return 저장된 메시지 목록
     */
    fun saveAll(messages: List<OutboxMessage>): List<OutboxMessage>

    /**
     * ID로 Outbox 메시지를 조회합니다.
     *
     * @param id 메시지 ID
     * @return 조회된 메시지 또는 null
     */
    fun findById(id: UUID): OutboxMessage?

    /**
     * 특정 상태의 Outbox 메시지들을 조회합니다.
     * FOR UPDATE SKIP LOCKED를 사용하여 동시성을 제어해야 합니다.
     *
     * @param status 조회할 메시지 상태
     * @param limit 조회할 최대 메시지 수
     * @return 조회된 메시지 목록
     */
    fun findByStatus(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage>

    /**
     * 메시지의 상태를 업데이트합니다.
     * 이 메서드는 updatedAt과 lastAttemptTime도 함께 업데이트해야 합니다.
     *
     * @param id 메시지 ID
     * @param newStatus 새로운 상태
     * @param incrementRetry 재시도 횟수 증가 여부
     * @return 업데이트된 메시지
     */
    fun updateStatus(
        id: UUID,
        newStatus: OutboxMessageStatus,
        incrementRetry: Boolean = false,
    ): OutboxMessage?

    /**
     * 특정 시간 이전에 생성된 실패 상태의 메시지들을 조회합니다.
     *
     * @param maxRetries 최대 재시도 횟수
     * @param limit 조회할 최대 메시지 수
     * @return 조회된 메시지 목록
     */
    fun findFailedMessagesExceedingRetryCount(
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage>

    fun findAndMarkForProcessing(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage>

    fun countByStatus(status: OutboxMessageStatus): Long

    fun incrementRetryCount(id: UUID): OutboxMessage?
}
