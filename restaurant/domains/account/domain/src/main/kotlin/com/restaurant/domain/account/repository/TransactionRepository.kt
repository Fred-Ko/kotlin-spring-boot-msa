package com.restaurant.domain.account.repository

import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionId
import com.restaurant.domain.account.vo.TransactionType

/**
 * 계좌 트랜잭션 리포지토리 인터페이스
 */
interface TransactionRepository {
    /**
     * ID로 트랜잭션을 찾습니다.
     */
    fun findById(id: TransactionId): Transaction?

    /**
     * 트랜잭션을 저장합니다.
     */
    fun save(transaction: Transaction): Transaction

    /**
     * 계좌 ID로 커서 기반 트랜잭션 목록을 조회합니다.
     */
    fun findByAccountIdWithCursor(
        accountId: AccountId,
        cursor: TransactionId?,
        limit: Int,
    ): List<Transaction>

    /**
     * 주문 ID로 트랜잭션을 조회합니다.
     */
    fun findByOrderId(orderId: OrderId): List<Transaction>

    /**
     * 계좌 ID와 트랜잭션 타입으로 커서 기반 트랜잭션 목록을 조회합니다.
     */
    fun findByAccountIdAndTypeWithCursor(
        accountId: AccountId,
        type: TransactionType,
        cursor: TransactionId?,
        limit: Int,
    ): List<Transaction>

    /**
     * 계좌 ID와 날짜 범위로 커서 기반 트랜잭션 목록을 조회합니다.
     */
    fun findByAccountIdAndTimestampBetweenWithCursor(
        accountId: AccountId,
        startTime: Long,
        endTime: Long,
        cursor: TransactionId?,
        limit: Int,
    ): List<Transaction>
}
