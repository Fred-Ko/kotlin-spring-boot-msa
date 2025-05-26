package com.restaurant.outbox.application.dto

import com.restaurant.outbox.application.dto.model.OutboxMessage
import com.restaurant.outbox.application.dto.model.OutboxMessageStatus

/**
 * Outbox 메시지 저장소에 대한 포트 인터페이스.
 */
interface OutboxMessageRepository {
    /**
     * 단일 Outbox 메시지를 저장합니다.
     * @param message 저장할 메시지
     * @return 저장된 메시지
     */
    fun save(message: OutboxMessage): OutboxMessage

    /**
     * 여러 Outbox 메시지를 저장합니다.
     * 이 메서드는 원자적으로 실행되어야 합니다 - 모든 메시지가 저장되거나 아무것도 저장되지 않아야 합니다.
     *
     * @param messages 저장할 메시지 목록
     */
    fun saveAll(messages: List<OutboxMessage>) // 반환 타입 Unit (없음)

    /**
     * ID로 Outbox 메시지를 조회합니다.
     *
     * @param id 메시지 ID
     * @return 조회된 메시지 또는 null
     */
    fun findById(id: Long): OutboxMessage?

    /**
     * 특정 상태의 Outbox 메시지들을 조회합니다.
     *
     * @param status 조회할 메시지 상태
     * @return 조회된 메시지 목록
     */
    fun findByStatus(status: OutboxMessageStatus): List<OutboxMessage>

    /**
     * 메시지의 상태를 업데이트합니다. (OutboxMessageStatus enum 사용)
     * 이 메서드는 updatedAt과 lastAttemptTime도 함께 업데이트해야 합니다.
     *
     * @param id 메시지 ID (Long)
     * @param newStatus 새로운 상태 (OutboxMessageStatus)
     * @param incrementRetry 재시도 횟수 증가 여부
     * @return 업데이트된 메시지
     */
    fun updateStatus(
        id: Long,
        newStatus: OutboxMessageStatus,
        incrementRetry: Boolean = false,
    ): OutboxMessage?

    /**
     * 특정 시간 이전에 생성된 실패 상태의 메시지들을 조회합니다. (이름 변경 또는 삭제 고려)
     * 이름이 역할과 맞지 않을 수 있음: 실제로는 재시도 횟수가 maxRetries *미만*인 것을 찾아야 함.
     * findMessagesToRetryByStatusAndRetryCountLessThan 로 대체 예정.
     *
     * @param maxRetries 최대 재시도 횟수 (이 값 *미만*인 것을 찾음)
     * @param limit 조회할 최대 메시지 수
     * @return 조회된 메시지 목록
     */
    fun findFailedMessagesExceedingRetryCount( // 이 메서드는 이름이 오해의 소지가 있음.
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage>

    /**
     * 특정 상태의 메시지를 조회하여 처리 중(PROCESSING) 상태로 변경합니다.
     * 동시성 제어를 위해 적절한 잠금 메커니즘을 사용해야 합니다.
     *
     * @param status 조회 및 처리할 메시지의 현재 상태 (일반적으로 PENDING)
     * @param limit 조회할 최대 메시지 수
     * @return 처리 대상으로 표시된 메시지 목록
     */
    fun findAndMarkForProcessing(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessage>

    /**
     * 특정 상태의 메시지 개수를 반환합니다.
     * @param status 조회할 메시지 상태
     * @return 해당 상태의 메시지 개수
     */
    fun countByStatus(status: OutboxMessageStatus): Long

    /**
     * 메시지의 재시도 횟수를 1 증가시킵니다.
     * lastAttemptTime과 updatedAt도 현재 시간으로 업데이트합니다.
     * @param id 메시지 ID
     * @return 업데이트된 메시지 또는 null (메시지를 찾지 못한 경우)
     */
    fun incrementRetryCount(id: Long): OutboxMessage?

    /**
     * 처리되지 않은 메시지를 조회합니다. (FOR UPDATE SKIP LOCKED 사용 가능성 고려)
     * @param batchSize 조회할 최대 메시지 수
     * @return 처리되지 않은 OutboxMessage 목록
     */
    fun findUnprocessedMessages(batchSize: Int): List<OutboxMessage>

    /**
     * 메시지 상태를 업데이트합니다. (이 메서드는 updateStatus(id, newStatus, incrementRetry)로 대체될 수 있음)
     * @param messageId 업데이트할 메시지 ID
     * @param status 새로운 상태 (OutboxMessageStatus)
     * @param retryCount 재시도 횟수
     */
    fun updateMessageStatus( // status 타입을 OutboxMessageStatus로 변경
        messageId: Long,
        status: OutboxMessageStatus,
        retryCount: Int,
    )

    /**
     * 특정 상태이고 재시도 횟수가 지정된 값 미만인 메시지 목록을 조회합니다.
     * 주로 재시도할 실패(FAILED) 메시지를 찾는데 사용됩니다.
     *
     * @param status 조회할 메시지의 상태
     * @param maxRetries 이 횟수 미만으로 재시도된 메시지만 조회
     * @param limit 조회할 최대 메시지 수
     * @return 조건에 맞는 OutboxMessage 목록
     */
    fun findByStatusAndRetryCountLessThan(
        status: OutboxMessageStatus,
        maxRetries: Int,
        limit: Int,
    ): List<OutboxMessage>
}
