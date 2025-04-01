package com.restaurant.domain.account.aggregate

import com.restaurant.domain.account.exception.InsufficientBalanceException
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.UserId

/**
 * 계좌 애그리게이트
 * 사용자에 연결된 계좌를 관리합니다.
 */
data class Account(
    val id: AccountId?,
    val userId: UserId,
    val balance: Money,
    val transactions: List<Transaction> = emptyList(),
) {
    /**
     * 계좌에서 금액을 차감합니다.
     *
     * @param amount 차감할 금액
     * @param orderId 주문 ID
     * @throws InsufficientBalanceException 잔액이 부족할 경우 발생
     */
    fun debit(
        amount: Money,
        orderId: OrderId,
    ): Account {
        if (!balance.isGreaterThanOrEqual(amount)) {
            throw InsufficientBalanceException(
                accountId = id ?: throw IllegalStateException("계좌 ID가 없습니다."),
                currentBalance = balance,
                requiredAmount = amount,
            )
        }

        val transaction = Transaction.debit(amount, orderId)
        return copy(
            balance = balance - amount,
            transactions = transactions + transaction,
        )
    }

    /**
     * 계좌에 금액을 추가합니다.
     *
     * @param amount 추가할 금액
     * @param orderId 주문 ID (취소 처리 시)
     */
    fun credit(
        amount: Money,
        orderId: OrderId,
    ): Account {
        val transaction = Transaction.credit(amount, orderId)
        return copy(
            balance = balance + amount,
            transactions = transactions + transaction,
        )
    }

    /**
     * 계좌에 입금합니다.
     *
     * @param amount 입금할 금액
     */
    fun deposit(amount: Money): Account = copy(balance = balance + amount)

    /**
     * 주문 ID로 트랜잭션을 찾습니다.
     */
    fun findTransactionByOrderId(orderId: OrderId): Transaction? = transactions.find { it.orderId == orderId }

    companion object {
        /**
         * 새 계좌를 생성합니다.
         *
         * @param userId 사용자 ID
         * @param initialBalance 초기 잔액 (기본값 0)
         */
        fun create(
            userId: UserId,
            initialBalance: Money = Money.ZERO,
        ): Account =
            Account(
                id = null,
                userId = userId,
                balance = initialBalance,
            )
    }
}

/**
 * 계좌 트랜잭션
 * 계좌의 입출금 내역을 추적합니다.
 */
data class Transaction(
    val type: TransactionType,
    val amount: Money,
    val orderId: OrderId,
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        fun debit(
            amount: Money,
            orderId: OrderId,
        ): Transaction =
            Transaction(
                type = TransactionType.DEBIT,
                amount = amount,
                orderId = orderId,
            )

        fun credit(
            amount: Money,
            orderId: OrderId,
        ): Transaction =
            Transaction(
                type = TransactionType.CREDIT,
                amount = amount,
                orderId = orderId,
            )
    }
}

enum class TransactionType {
    DEBIT, // 출금 (계좌에서 차감)
    CREDIT, // 입금 (계좌로 추가)
}
