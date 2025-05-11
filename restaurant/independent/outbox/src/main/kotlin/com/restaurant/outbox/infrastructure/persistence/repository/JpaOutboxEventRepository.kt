package com.restaurant.outbox.infrastructure.persistence.repository

import com.restaurant.outbox.application.port.model.OutboxMessageStatus
import com.restaurant.outbox.infrastructure.entity.OutboxEventEntity
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaOutboxEventRepository : JpaRepository<OutboxEventEntity, Long> {
    fun findByStatus(status: OutboxMessageStatus): List<OutboxEventEntity>

    fun findTopByStatusOrderByCreatedAtAsc(
        status: OutboxMessageStatus,
        pageable: Pageable,
    ): List<OutboxEventEntity>

    fun findByStatusAndRetryCountGreaterThanEqual(
        status: OutboxMessageStatus,
        retryCount: Int,
        pageable: Pageable,
    ): List<OutboxEventEntity>

    fun countByStatus(status: OutboxMessageStatus): Long

    /**
     * 특정 상태의 메시지들을 조회하되, 재시도 횟수가 특정 값 미만인 메시지들만 조회합니다.
     * 이 메서드는 주로 실패한 메시지 중 아직 재시도 한도에 도달하지 않은 메시지를 찾는데 사용될 수 있습니다.
     */
    @Query(
        "SELECT oe FROM OutboxEventEntity oe WHERE oe.status = :status AND oe.retryCount < :maxRetries ORDER BY oe.createdAt ASC",
    )
    fun findByStatusAndRetryCountLessThan(
        @Param("status") status: OutboxMessageStatus, // OutboxMessageStatus로 수정
        @Param("maxRetries") maxRetries: Int,
        pageable: Pageable,
    ): List<OutboxEventEntity>

    /**
     * 처리되지 않은 이벤트를 조회하며 비관적 잠금을 사용합니다. (SKIP LOCKED 사용)
     * 데이터베이스에 따라 @QueryHint 사용이 필요할 수 있습니다.
     * 예시: PostgreSQL의 경우 FOR UPDATE SKIP LOCKED
     * Spring Data JPA 3.x 이상에서는 @Lock(LockModeType.PESSIMISTIC_WRITE)와 함께
     * spring.jpa.properties.jakarta.persistence.lock.timeout=0 (또는 DB 특화 힌트) 설정 고려.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM OutboxEventEntity e WHERE e.status = com.restaurant.outbox.application.port.model.OutboxMessageStatus.PENDING ORDER BY e.createdAt ASC")
    fun findUnprocessedEventsWithLock(pageable: Pageable): List<OutboxEventEntity>
}
