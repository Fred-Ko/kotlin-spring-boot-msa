package com.restaurant.account.application.exception

import com.restaurant.account.application.error.AccountApplicationErrorCodes
import com.restaurant.common.application.exception.ApplicationException

sealed class AccountApplicationException(
    override val errorCode: AccountApplicationErrorCodes,
    message: String? = errorCode.message,
    cause: Throwable? = null,
) : ApplicationException(message, cause) {
    class AccountNotFound(
        userId: String,
    ) : AccountApplicationException(
            AccountApplicationErrorCodes.ACCOUNT_NOT_FOUND,
            "Account not found for user ID: $userId",
        )
}
