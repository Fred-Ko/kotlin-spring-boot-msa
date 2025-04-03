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
    val id: TransactionId?,
    val type: TransactionType,
    val amount: Money,
    val orderId: OrderId,
    val accountId: AccountId?,
    val timestamp: Long = System.currentTimeMillis(),
) {
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
            accountId: AccountId?,
        ): Transaction =
            Transaction(
                id = null,
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
            accountId: AccountId?,
        ): Transaction =
            Transaction(
                id = null,
                type = TransactionType.CREDIT,
                amount = amount,
                orderId = orderId,
                accountId = accountId,
            )
    }
}
