package com.restaurant.account.domain.error

import com.restaurant.common.domain.error.ErrorCode

enum class AccountDomainErrorCodes(
    override val code: String,
    override val message: String,
) : ErrorCode {
    INVALID_ACCOUNT_ID_FORMAT("ACCOUNT-DOMAIN-001", "Invalid account ID format"),
    INVALID_USER_ID_FORMAT("ACCOUNT-DOMAIN-002", "Invalid user ID format"),
    INVALID_INITIAL_BALANCE("ACCOUNT-DOMAIN-003", "Initial balance cannot be negative"),
    INSUFFICIENT_FUNDS("ACCOUNT-DOMAIN-004", "Insufficient funds for withdrawal"),
    ACCOUNT_NOT_ACTIVE("ACCOUNT-DOMAIN-005", "Account is not active"),
    ACCOUNT_ALREADY_CLOSED("ACCOUNT-DOMAIN-006", "Account is already closed"),
    CANNOT_WITHDRAW_NEGATIVE_AMOUNT("ACCOUNT-DOMAIN-007", "Cannot withdraw a negative amount"),
    CANNOT_DEPOSIT_NEGATIVE_AMOUNT("ACCOUNT-DOMAIN-008", "Cannot deposit a negative amount"),
}
 