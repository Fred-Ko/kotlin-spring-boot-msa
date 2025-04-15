package com.restaurant.domain.account.aggregate

import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionId
import com.restaurant.domain.account.vo.TransactionType

/**
 * 계좌 트랜잭션 애그리게이트 루트
 * 계좌의 입출금 내역을 독립적으로 관리합니다.
 */
data class Transaction(
    val id: TransactionId,
    val type: TransactionType,
    val amount: Money,
    val orderId: OrderId,
    val accountId: AccountId,
    val cancelled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
) {
    /**
     * 트랜잭션이 취소되었는지 확인합니다.
     *
     * @return 취소 여부
     */
    fun isCancelled(): Boolean = cancelled

    /**
     * 트랜잭션을 취소 상태로 변경합니다.
     *
     * @return 취소된 트랜잭션
     */
    fun cancel(): Transaction = copy(cancelled = true)

    companion object {
        /**
         * 출금 트랜잭션 생성
         *
         * @param amount 출금 금액
         * @param orderId 주문 ID
         * @param accountId 계좌 ID
         */
        fun debit(
            amount: Money,
            orderId: OrderId,
            accountId: AccountId,
        ): Transaction =
            Transaction(
                id = TransactionId.of(0L),
                type = TransactionType.DEBIT,
                amount = amount,
                orderId = orderId,
                accountId = accountId,
            )

        /**
         * 입금 트랜잭션 생성
         *
         * @param amount 입금 금액
         * @param orderId 주문 ID
         * @param accountId 계좌 ID
         */
        fun credit(
            amount: Money,
            orderId: OrderId,
            accountId: AccountId,
        ): Transaction =
            Transaction(
                id = TransactionId.of(0L),
                type = TransactionType.CREDIT,
                amount = amount,
                orderId = orderId,
                accountId = accountId,
            )

        /**
         * 기존 트랜잭션을 재구성합니다.
         *
         * @param id 트랜잭션 ID
         * @param type 트랜잭션 타입
         * @param amount 금액
         * @param orderId 주문 ID
         * @param accountId 계좌 ID
         * @param cancelled 취소 여부
         * @param timestamp 타임스탬프
         */
        fun reconstitute(
            id: TransactionId,
            type: TransactionType,
            amount: Money,
            orderId: OrderId,
            accountId: AccountId,
            cancelled: Boolean,
            timestamp: Long,
        ): Transaction =
            Transaction(
                id = id,
                type = type,
                amount = amount,
                orderId = orderId,
                accountId = accountId,
                cancelled = cancelled,
                timestamp = timestamp,
            )
    }
}
