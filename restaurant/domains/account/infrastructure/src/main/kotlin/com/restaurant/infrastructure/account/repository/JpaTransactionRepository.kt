package com.restaurant.infrastructure.account.repository

import com.restaurant.infrastructure.account.entity.TransactionEntity
import com.restaurant.infrastructure.account.entity.TransactionTypeEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 트랜잭션 JPA 리포지토리
 */
@Repository
interface JpaTransactionRepository : JpaRepository<TransactionEntity, Long> {
    /**
     * 계좌 ID로 커서 기반 조회 (ID 내림차순)
     */
    @Query("SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId AND (:cursor IS NULL OR t.id < :cursor) ORDER BY t.id DESC")
    fun findByAccountIdWithCursor(
        @Param("accountId") accountId: Long,
        @Param("cursor") cursor: Long?,
        pageable: Pageable,
    ): List<TransactionEntity>

    /**
     * 주문 ID로 트랜잭션 조회
     */
    fun findByOrderId(orderId: String): List<TransactionEntity>

    /**
     * 계좌 ID와 타입으로 커서 기반 조회 (ID 내림차순)
     */
    @Query(
        "SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId AND t.type = :type AND (:cursor IS NULL OR t.id < :cursor) ORDER BY t.id DESC",
    )
    fun findByAccountIdAndTypeWithCursor(
        @Param("accountId") accountId: Long,
        @Param("type") type: TransactionTypeEntity,
        @Param("cursor") cursor: Long?,
        pageable: Pageable,
    ): List<TransactionEntity>

    /**
     * 계좌 ID와 날짜 범위로 커서 기반 조회 (ID 내림차순)
     */
    @Query(
        "SELECT t FROM TransactionEntity t WHERE t.accountId = :accountId AND t.timestamp BETWEEN :startTime AND :endTime AND (:cursor IS NULL OR t.id < :cursor) ORDER BY t.id DESC",
    )
    fun findByAccountIdAndTimestampBetweenWithCursor(
        @Param("accountId") accountId: Long,
        @Param("startTime") startTime: Long,
        @Param("endTime") endTime: Long,
        @Param("cursor") cursor: Long?,
        pageable: Pageable,
    ): List<TransactionEntity>
}
