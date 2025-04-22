package com.restaurant.domain.user.error

import com.restaurant.common.core.error.ErrorCode
import com.restaurant.common.core.exception.DomainException

/**
 * User 도메인 관련 예외 최상위 클래스
 */
sealed class UserDomainException(
    override val errorCode: ErrorCode,
    override val message: String,
    override val cause: Throwable? = null,
) : DomainException(message) {
    // User 관련 예외
    sealed class User(
        errorCode: ErrorCode,
        message: String,
        cause: Throwable? = null,
    ) : UserDomainException(errorCode, message, cause) {
        data class NotFound(
            val userId: String,
        ) : User(
                UserDomainErrorCodes.USER_NOT_FOUND,
                "User not found with ID: $userId",
            )

        data class DuplicateEmail(
            val email: String,
        ) : User(
                UserDomainErrorCodes.DUPLICATE_EMAIL,
                "Email already exists: $email",
            )

        data class InvalidCredentials(
            override val errorCode: UserDomainErrorCodes = UserDomainErrorCodes.PASSWORD_MISMATCH,
            override val message: String = "Invalid username or password",
        ) : User(errorCode, message)
    }

    // Address 관련 예외
    sealed class Address(
        errorCode: ErrorCode,
        message: String,
        cause: Throwable? = null,
    ) : UserDomainException(errorCode, message, cause) {
        data class NotFound(
            val userId: String,
            val addressId: String,
        ) : Address(
                UserDomainErrorCodes.ADDRESS_NOT_FOUND,
                "Address not found for userId: $userId, addressId: $addressId",
            )

        data class CannotRemoveLastAddress(
            val addressId: String,
        ) : Address(
                UserDomainErrorCodes.CANNOT_REMOVE_LAST_ADDRESS,
                "Cannot remove last address: $addressId",
            )
    }

    // Validation 관련 예외
    sealed class Validation(
        errorCode: ErrorCode,
        message: String,
        cause: Throwable? = null,
    ) : UserDomainException(errorCode, message, cause) {
        data class InvalidEmailFormat(
            val email: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_INPUT,
                "Invalid email format: $email",
            )

        data class InvalidPasswordFormat(
            val reason: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_PASSWORD_FORMAT,
                reason,
            )

        data class InvalidNameFormat(
            val name: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_INPUT,
                "Name cannot be blank: '$name'",
            )

        data class InvalidAddressFormat(
            val reason: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_INPUT,
                reason,
            )

        data class InvalidPhoneNumberFormat(
            val phone: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_INPUT,
                "Invalid phone number format: $phone",
            )
    }
}

// UserDomainErrorCodes Enum에 필요한 추가 코드 (예시)
