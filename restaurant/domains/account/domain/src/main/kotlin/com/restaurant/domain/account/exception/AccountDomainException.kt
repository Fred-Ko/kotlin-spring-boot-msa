package com.restaurant.domain.account.exception

import com.restaurant.common.core.exception.DomainException
import com.restaurant.domain.account.error.AccountDomainErrorCode
import com.restaurant.domain.account.error.DefaultAccountDomainErrorCode
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.TransactionId

/**
 * Account 도메인의 기본 예외 클래스
 */
sealed class AccountDomainException(
    override val message: String,
    open val errorCode: AccountDomainErrorCode,
) : DomainException(message) {
    /**
     * 계좌 관련 예외
     */
    sealed class Account(
        message: String,
        override val errorCode: AccountDomainErrorCode,
    ) : AccountDomainException(message, errorCode) {
        /**
         * 계좌를 찾을 수 없을 때 발생하는 예외
         */
        data class NotFound(
            val accountId: AccountId,
            override val errorCode: AccountDomainErrorCode = DefaultAccountDomainErrorCode.ACCOUNT_NOT_FOUND,
        ) : Account("계좌를 찾을 수 없습니다: ${accountId.value}", errorCode)

        /**
         * 계좌의 잔액이 부족할 때 발생하는 예외
         */
        data class InsufficientBalance(
            val accountId: AccountId,
            val currentBalance: Money,
            val requiredAmount: Money,
            override val errorCode: AccountDomainErrorCode = DefaultAccountDomainErrorCode.INSUFFICIENT_BALANCE,
        ) : Account("계좌 ${accountId.value}의 잔액이 부족합니다. 잔액: ${currentBalance.value}, 필요 금액: ${requiredAmount.value}", errorCode)
    }

    /**
     * 트랜잭션 관련 예외
     */
    sealed class Transaction(
        message: String,
        override val errorCode: AccountDomainErrorCode,
    ) : AccountDomainException(message, errorCode) {
        /**
         * 트랜잭션을 찾을 수 없을 때 발생하는 예외
         */
        data class NotFound(
            val transactionId: TransactionId,
            override val errorCode: AccountDomainErrorCode = DefaultAccountDomainErrorCode.TRANSACTION_NOT_FOUND,
        ) : Transaction("트랜잭션을 찾을 수 없습니다: ${transactionId.value}", errorCode)

        /**
         * 이미 취소된 결제를 취소하려고 할 때 발생하는 예외
         */
        data class AlreadyCancelled(
            val transactionId: TransactionId,
            override val errorCode: AccountDomainErrorCode = DefaultAccountDomainErrorCode.TRANSACTION_ALREADY_CANCELLED,
        ) : Transaction("이미 취소된 결제입니다: ${transactionId.value}", errorCode)
    }
}
