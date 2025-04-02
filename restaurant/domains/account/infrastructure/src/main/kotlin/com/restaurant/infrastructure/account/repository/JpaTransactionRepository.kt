package com.restaurant.infrastructure.account.repository

import com.restaurant.infrastructure.account.entity.TransactionEntity
import com.restaurant.infrastructure.account.entity.TransactionTypeEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 계좌 트랜잭션 JPA 리포지토리
 */
@Repository
interface JpaTransactionRepository : JpaRepository<TransactionEntity, Long> {
    /**
     * 계좌 ID로 트랜잭션 목록을 페이징하여 조회합니다.
     */
    fun findByAccountId(
        accountId: Long,
        pageable: Pageable,
    ): Page<TransactionEntity>

    /**
     * 주문 ID로 트랜잭션을 조회합니다.
     */
    fun findByOrderId(orderId: String): List<TransactionEntity>

    /**
     * 계좌 ID와 트랜잭션 타입으로 트랜잭션 목록을 페이징하여 조회합니다.
     */
    fun findByAccountIdAndType(
        accountId: Long,
        type: TransactionTypeEntity,
        pageable: Pageable,
    ): Page<TransactionEntity>

    /**
     * 계좌 ID와 날짜 범위로 트랜잭션 목록을 조회합니다.
     */
    @Query("SELECT t FROM TransactionEntity t WHERE t.account.id = :accountId AND t.timestamp BETWEEN :startTime AND :endTime")
    fun findByAccountIdAndTimestampBetween(
        @Param("accountId") accountId: Long,
        @Param("startTime") startTime: Long,
        @Param("endTime") endTime: Long,
        pageable: Pageable,
    ): Page<TransactionEntity>
}
