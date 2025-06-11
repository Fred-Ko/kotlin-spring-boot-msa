package com.restaurant.account.domain.exception

import com.restaurant.account.domain.error.AccountDomainErrorCodes
import com.restaurant.common.domain.exception.DomainException

/**
 * Sealed class representing all possible domain exceptions for the Account aggregate. (Rule 68)
 */
sealed class AccountDomainException(
    override val errorCode: AccountDomainErrorCodes,
    message: String? = errorCode.message,
    cause: Throwable? = null,
) : DomainException(message, cause) {
    /**
     * Validation-related exceptions
     */
    sealed class Validation(
        override val errorCode: AccountDomainErrorCodes,
        message: String = errorCode.message,
        cause: Throwable? = null,
    ) : AccountDomainException(errorCode, message, cause) {
        class InvalidAccountIdFormat(
            value: String,
        ) : Validation(
                AccountDomainErrorCodes.INVALID_ACCOUNT_ID_FORMAT,
                "Invalid account ID format: $value",
            )

        class InvalidUserIdFormat(
            value: String,
        ) : Validation(
                AccountDomainErrorCodes.INVALID_USER_ID_FORMAT,
                "Invalid user ID format: $value",
            )

        class InvalidInitialBalance(
            value: Long,
        ) : Validation(
                AccountDomainErrorCodes.INVALID_INITIAL_BALANCE,
                "Initial balance cannot be negative: $value",
            )

        class CannotWithdrawNegativeAmount(
            value: Long,
        ) : Validation(
                AccountDomainErrorCodes.CANNOT_WITHDRAW_NEGATIVE_AMOUNT,
                "Cannot withdraw a negative amount: $value",
            )

        class CannotDepositNegativeAmount(
            value: Long,
        ) : Validation(
                AccountDomainErrorCodes.CANNOT_DEPOSIT_NEGATIVE_AMOUNT,
                "Cannot deposit a negative amount: $value",
            )
    }

    /**
     * Operation-related exceptions
     */
    sealed class Operation(
        override val errorCode: AccountDomainErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : AccountDomainException(errorCode, message, cause) {
        class InsufficientFunds(
            accountId: String,
            balance: Long,
            withdrawalAmount: Long,
        ) : Operation(
                AccountDomainErrorCodes.INSUFFICIENT_FUNDS,
                "Insufficient funds in account $accountId. Current balance is $balance, but tried to withdraw $withdrawalAmount.",
            )

        class AccountNotActive(
            accountId: String,
        ) : Operation(
                AccountDomainErrorCodes.ACCOUNT_NOT_ACTIVE,
                "Account $accountId is not active.",
            )

        class AccountAlreadyClosed(
            accountId: String,
        ) : Operation(
                AccountDomainErrorCodes.ACCOUNT_ALREADY_CLOSED,
                "Account $accountId is already closed.",
            )
    }
}
