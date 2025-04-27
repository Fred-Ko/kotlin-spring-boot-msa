package com.restaurant.user.domain.exception

import com.restaurant.common.core.exception.DomainException
import com.restaurant.user.domain.error.UserDomainErrorCodes

/**
 * Sealed class representing all possible domain exceptions for the User aggregate. (Rule 68)
 */
sealed class UserDomainException(
    override val errorCode: UserDomainErrorCodes,
    message: String? = errorCode.message,
    cause: Throwable? = null,
) : DomainException(errorCode, message, cause) {
    /**
     * Validation-related exceptions
     */
    sealed class Validation(
        errorCode: UserDomainErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : UserDomainException(errorCode, message, cause) {
        class InvalidEmailFormat(
            value: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_EMAIL_FORMAT,
                "Invalid email format: $value",
            )

        class InvalidUsernameFormat(
            value: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_USERNAME_FORMAT,
                "Invalid username format: $value",
            )

        class InvalidUserIdFormat(
            value: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_USER_ID_FORMAT,
                "Invalid user ID format: $value",
            )

        class InvalidPasswordFormat(
            message: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_PASSWORD_FORMAT,
                message,
            )

        class InvalidNameFormat(
            value: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_NAME_FORMAT,
                "Invalid name format: $value",
            )

        class InvalidPhoneNumberFormat(
            value: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_PHONE_NUMBER_FORMAT,
                "Invalid phone number format: $value",
            )

        class InvalidAddressFormat(
            message: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_ADDRESS_FORMAT,
                message,
            )

        class InvalidAddressIdFormat(
            value: String,
        ) : Validation(
                UserDomainErrorCodes.INVALID_ADDRESS_ID_FORMAT,
                "Invalid address ID format: $value",
            )
    }

    /**
     * User-related exceptions
     */
    sealed class User(
        errorCode: UserDomainErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : UserDomainException(errorCode, message, cause) {
        class NotFound(
            userId: String,
        ) : User(
                UserDomainErrorCodes.USER_NOT_FOUND,
                "User not found with ID: $userId",
            )

        class DuplicateUsername(
            username: String,
        ) : User(
                UserDomainErrorCodes.DUPLICATE_USERNAME,
                "Username already exists: $username",
            )

        class DuplicateEmail(
            email: String,
        ) : User(
                UserDomainErrorCodes.DUPLICATE_EMAIL,
                "Email already exists: $email",
            )

        class PasswordMismatch : User(UserDomainErrorCodes.PASSWORD_MISMATCH)

        class AlreadyWithdrawn : User(UserDomainErrorCodes.USER_ALREADY_WITHDRAWN)

        class InvalidCredentials(
            username: String,
        ) : User(
                UserDomainErrorCodes.INVALID_CREDENTIALS,
                "Invalid credentials for username: $username",
            )

        class AdminCannotBeWithdrawn :
            User(
                UserDomainErrorCodes.ADMIN_CANNOT_BE_WITHDRAWN,
                "Admin user cannot be withdrawn.",
            )
    }

    /**
     * Address-related exceptions
     */
    sealed class Address(
        errorCode: UserDomainErrorCodes,
        message: String? = errorCode.message,
        cause: Throwable? = null,
    ) : UserDomainException(errorCode, message, cause) {
        class NotFound(
            addressId: String,
        ) : Address(
                UserDomainErrorCodes.ADDRESS_NOT_FOUND,
                "Address not found with ID: $addressId",
            )

        class DefaultAddressNotFound(
            message: String = "Default address ID does not exist in the address list.",
        ) : Address(
                UserDomainErrorCodes.DEFAULT_ADDRESS_NOT_FOUND,
                message,
            )

        class DuplicateAddressId(
            addressId: String,
        ) : Address(
                UserDomainErrorCodes.DUPLICATE_ADDRESS_ID,
                "Address ID already exists: $addressId",
            )

        class IdMismatch(
            existingId: String,
            newId: String,
        ) : Address(
                UserDomainErrorCodes.ADDRESS_ID_MISMATCH,
                "Address ID mismatch: expected $existingId, got $newId",
            )

        class LimitExceeded(
            limit: Int,
        ) : Address(
                UserDomainErrorCodes.MAX_ADDRESS_LIMIT_EXCEEDED,
                "Cannot add more addresses, limit is $limit",
            )

        class CannotDeleteDefault(
            message: String = "Cannot remove the default address.",
        ) : Address(
                UserDomainErrorCodes.DEFAULT_ADDRESS_CANNOT_BE_REMOVED,
                message,
            )

        class CannotDeleteLast(
            message: String = "Cannot remove the last address.",
        ) : Address(
                UserDomainErrorCodes.CANNOT_REMOVE_LAST_ADDRESS,
                message,
            )

        class MultipleDefaultsOnInit(
            val reason: String = "Cannot initialize user with multiple default addresses.",
        ) : Address(
                UserDomainErrorCodes.INVALID_ADDRESS_FORMAT,
                reason,
            )
    }

    class PersistenceError(
        message: String,
        cause: Throwable? = null,
    ) : UserDomainException(
            UserDomainErrorCodes.PERSISTENCE_ERROR,
            message,
            cause,
        )
}
