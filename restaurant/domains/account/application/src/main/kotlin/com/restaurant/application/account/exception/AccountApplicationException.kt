package com.restaurant.application.account.exception

import com.restaurant.application.account.common.AccountErrorCode
import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.TransactionId

/**
 * 계좌 애플리케이션 예외의 부모 클래스
 */
sealed class AccountApplicationException(
    override val message: String,
    open val errorCode: ErrorCode,
) : ApplicationException(message) {
    /**
     * 계좌 조회 관련 예외
     */
    sealed class Query(
        message: String,
        override val errorCode: ErrorCode,
    ) : AccountApplicationException(message, errorCode) {
        /**
         * 계좌를 찾을 수 없을 때 발생하는 예외
         */
        data class NotFound(
            val accountId: String,
            override val errorCode: ErrorCode = AccountErrorCode.NOT_FOUND,
        ) : Query("계좌를 찾을 수 없습니다: $accountId", errorCode)
    }

    /**
     * 계좌 트랜잭션 관련 예외
     */
    sealed class Transaction(
        message: String,
        override val errorCode: ErrorCode,
    ) : AccountApplicationException(message, errorCode) {
        /**
         * 잔액이 부족할 때 발생하는 예외
         */
        data class InsufficientBalance(
            val accountId: AccountId,
            val currentBalance: Money,
            val requiredAmount: Money,
            override val errorCode: ErrorCode = AccountErrorCode.INSUFFICIENT_BALANCE,
        ) : Transaction(
                "계좌 ${accountId.value}의 잔액이 부족합니다. 현재 잔액: ${currentBalance.value}, 필요 금액: ${requiredAmount.value}",
                errorCode,
            )

        /**
         * 트랜잭션을 찾을 수 없을 때 발생하는 예외
         */
        data class NotFound(
            val transactionId: TransactionId,
            override val errorCode: ErrorCode = AccountErrorCode.TRANSACTION_NOT_FOUND,
        ) : Transaction("트랜잭션을 찾을 수 없습니다: ${transactionId.value}", errorCode)

        /**
         * 이미 취소된 트랜잭션을 취소하려고 할 때 발생하는 예외
         */
        data class AlreadyCancelled(
            val transactionId: TransactionId,
            override val errorCode: ErrorCode = AccountErrorCode.TRANSACTION_ALREADY_CANCELLED,
        ) : Transaction("이미 취소된 트랜잭션입니다: ${transactionId.value}", errorCode)
    }

    /**
     * 시스템 오류 관련 예외
     */
    data class SystemError(
        val errorMessage: String,
        override val errorCode: ErrorCode = AccountErrorCode.SYSTEM_ERROR,
    ) : AccountApplicationException("시스템 오류가 발생했습니다: $errorMessage", errorCode)
}
