package com.restaurant.domain.account.aggregate

import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.UserId

/**
 * 계좌 애그리게이트
 * 사용자에 연결된 계좌를 관리합니다.
 */
data class Account(
    val id: AccountId,
    val userId: UserId,
    val balance: Money,
) {
    /**
     * 계좌에서 금액을 차감합니다.
     *
     * @param amount 차감할 금액
     * @throws AccountDomainException.Account.InsufficientBalance 잔액이 부족할 경우 발생
     */
    fun debit(amount: Money): Account {
        if (!balance.isGreaterThanOrEqual(amount)) {
            throw AccountDomainException.Account.InsufficientBalance(
                accountId = id,
                currentBalance = balance,
                requiredAmount = amount,
            )
        }

        return copy(
            balance = balance - amount,
        )
    }

    /**
     * 계좌에 금액을 추가합니다.
     *
     * @param amount 추가할 금액
     */
    fun credit(amount: Money): Account =
        copy(
            balance = balance + amount,
        )

    /**
     * 계좌에 입금합니다.
     *
     * @param amount 입금할 금액
     */
    fun deposit(amount: Money): Account = copy(balance = balance + amount)

    companion object {
        /**
         * 새 계좌를 생성합니다. (ID는 부여되지 않은 상태)
         *
         * @param userId 사용자 ID
         * @param initialBalance 초기 잔액 (기본값 0)
         */
        fun create(
            userId: UserId,
            initialBalance: Money = Money.ZERO,
        ): Account =
            Account(
                id = AccountId.of(0L),
                userId = userId,
                balance = initialBalance,
            )

        /**
         * 기존 계좌를 재구성합니다.
         *
         * @param id 계좌 ID
         * @param userId 사용자 ID
         * @param balance 잔액
         */
        fun reconstitute(
            id: AccountId,
            userId: UserId,
            balance: Money,
        ): Account =
            Account(
                id = id,
                userId = userId,
                balance = balance,
            )
    }
}
