package com.restaurant.domain.account.repository

import com.restaurant.domain.account.entity.Transaction
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * 계좌 트랜잭션 리포지토리 인터페이스
 */
interface TransactionRepository {
    /**
     * 계좌 ID로 트랜잭션 목록을 페이징하여 조회합니다.
     */
    fun findByAccountId(
        accountId: AccountId,
        pageable: Pageable,
    ): Page<Transaction>

    /**
     * 주문 ID로 트랜잭션을 조회합니다.
     */
    fun findByOrderId(orderId: OrderId): List<Transaction>

    /**
     * 계좌 ID와 트랜잭션 타입으로 트랜잭션 목록을 페이징하여 조회합니다.
     */
    fun findByAccountIdAndType(
        accountId: AccountId,
        type: TransactionType,
        pageable: Pageable,
    ): Page<Transaction>

    /**
     * 계좌 ID와 날짜 범위로 트랜잭션 목록을 조회합니다.
     */
    fun findByAccountIdAndTimestampBetween(
        accountId: AccountId,
        startTime: Long,
        endTime: Long,
        pageable: Pageable,
    ): Page<Transaction>
}
