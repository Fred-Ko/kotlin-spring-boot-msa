package com.ddd.user.application.exception

import com.ddd.support.exception.ApplicationException

sealed class UserApplicationException(override val message: String, cause: Throwable? = null) :
        ApplicationException(message, cause) {
    class UserNotFound(id: String, cause: Throwable? = null) :
            UserApplicationException("User not found: $id", cause)
    class EmailAlreadyExists(email: String, cause: Throwable? = null) :
            UserApplicationException("Email already exists: $email", cause)
    class UserCreationFailed(id: String, cause: Throwable? = null) :
            UserApplicationException("User creation failed: $id", cause)
    class DeleteUserFailed(id: String, cause: Throwable? = null) :
            UserApplicationException("Delete user failed: $id", cause)
    class UpdateUserFailed(id: String, cause: Throwable? = null) :
            UserApplicationException("Update user failed: $id", cause)

    class UserAlreadyExists(email: String, cause: Throwable? = null) :
            UserApplicationException("User already exists: $email", cause)

    class GetUsersFailed(page: Int, size: Int, cause: Throwable? = null) :
            UserApplicationException("Get users failed: page=$page, size=$size", cause)

    class GetUserFailed(id: String, cause: Throwable? = null) :
            UserApplicationException("Get user failed: $id", cause)
}
