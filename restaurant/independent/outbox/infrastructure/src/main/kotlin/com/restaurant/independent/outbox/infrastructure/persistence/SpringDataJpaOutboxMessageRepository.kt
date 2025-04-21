package com.restaurant.independent.outbox.infrastructure.persistence

import com.restaurant.independent.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.independent.outbox.infrastructure.entity.OutboxMessageEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

/**
 * Outbox 메시지 엔티티를 위한 Spring Data JPA Repository 인터페이스.
 */
interface SpringDataJpaOutboxMessageRepository : JpaRepository<OutboxMessageEntity, UUID> {
    /**
     * 특정 상태의 메시지들을 조회합니다.
     */
    fun findByStatusOrderByCreatedAtAsc(
        status: OutboxMessageStatus,
        limit: Int,
    ): List<OutboxMessageEntity>

    /**
     * 특정 상태의 메시지들을 처리 중 상태로 업데이트하고 조회합니다.
     * 동시성 제어를 위해 SELECT FOR UPDATE SKIP LOCKED를 사용합니다.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        value = "SELECT o FROM OutboxMessageEntity o WHERE o.status = :status ORDER BY o.createdAt ASC LIMIT :limit",
        nativeQuery = false,
    )
    fun findAndLockByStatus(
        @Param("status") status: OutboxMessageStatus,
        @Param("limit") limit: Int,
    ): List<OutboxMessageEntity>

    /**
     * 특정 상태의 메시지 수를 조회합니다.
     */
    fun countByStatus(status: OutboxMessageStatus): Long

    /**
     * 특정 재시도 횟수를 초과한 실패 상태의 메시지들을 조회합니다.
     */
    fun findByStatusAndRetryCountGreaterThanOrderByCreatedAtAsc(
        status: OutboxMessageStatus,
        retryCount: Int,
        limit: Int,
    ): List<OutboxMessageEntity>
}
