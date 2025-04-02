package com.restaurant.domain.account.aggregate

import com.restaurant.domain.account.entity.Transaction
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
    val recentTransactions: List<Transaction> = emptyList(), // 최근 트랜잭션만 유지
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

        val transaction =
            Transaction.debit(
                amount = amount,
                orderId = orderId,
                accountId = id?.value,
            )

        // 최근 트랜잭션 목록 유지 (최대 10개만)
        val updatedTransactions =
            (recentTransactions + transaction)
                .takeLast(MAX_RECENT_TRANSACTIONS)

        return copy(
            balance = balance - amount,
            recentTransactions = updatedTransactions,
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
        val transaction =
            Transaction.credit(
                amount = amount,
                orderId = orderId,
                accountId = id?.value,
            )

        // 최근 트랜잭션 목록 유지 (최대 10개만)
        val updatedTransactions =
            (recentTransactions + transaction)
                .takeLast(MAX_RECENT_TRANSACTIONS)

        return copy(
            balance = balance + amount,
            recentTransactions = updatedTransactions,
        )
    }

    /**
     * 계좌에 입금합니다.
     *
     * @param amount 입금할 금액
     */
    fun deposit(amount: Money): Account = copy(balance = balance + amount)

    companion object {
        // 애그리거트에 유지할 최근 트랜잭션 수
        const val MAX_RECENT_TRANSACTIONS = 10

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
