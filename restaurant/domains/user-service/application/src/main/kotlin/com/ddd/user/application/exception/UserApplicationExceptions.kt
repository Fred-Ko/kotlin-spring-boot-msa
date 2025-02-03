package com.ddd.user.application.exception

import com.ddd.support.exception.ApplicationException


sealed class UserApplicationException(override val message: String, cause: Throwable? = null) : ApplicationException(message, cause) {
    class UserNotFound(id: String, cause: Throwable? = null) : UserApplicationException("User not found: $id", cause)
    class EmailAlreadyExists(email: String, cause: Throwable? = null) : UserApplicationException("Email already exists: $email", cause)
}