package com.restaurant.domain.account.entity

import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionType

/**
 * 계좌 트랜잭션
 * 계좌의 입출금 내역을 추적합니다.
 */
data class Transaction(
    val type: TransactionType,
    val amount: Money,
    val orderId: OrderId,
    val accountId: Long? = null, // Account ID 참조
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        fun debit(
            amount: Money,
            orderId: OrderId,
            accountId: Long? = null,
        ): Transaction =
            Transaction(
                type = TransactionType.DEBIT,
                amount = amount,
                orderId = orderId,
                accountId = accountId,
            )

        fun credit(
            amount: Money,
            orderId: OrderId,
            accountId: Long? = null,
        ): Transaction =
            Transaction(
                type = TransactionType.CREDIT,
                amount = amount,
                orderId = orderId,
                accountId = accountId,
            )
    }
}
