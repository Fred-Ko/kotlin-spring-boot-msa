package com.restaurant.account.application.error

import com.restaurant.common.domain.error.ErrorCode

enum class AccountApplicationErrorCodes(
    override val code: String,
    override val message: String,
) : ErrorCode {
    ACCOUNT_NOT_FOUND("ACCOUNT-APP-001", "Account not found for the given user ID"),
}
