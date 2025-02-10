package com.ddd.user.domain.exception

import com.ddd.support.exception.DomainException
import java.util.UUID

sealed class UserDomainException(override val message: String) : DomainException(message) {
    class UserNotFound(id: UUID) : UserDomainException("User not found: $id")
    class EmailAlreadyExists(email: String) : UserDomainException("Email already exists: $email")
}
