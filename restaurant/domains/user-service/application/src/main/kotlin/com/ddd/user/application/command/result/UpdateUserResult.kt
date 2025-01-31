package com.ddd.user.application.command.result

import com.ddd.user.domain.model.aggregate.User

sealed class UpdateUserResult {
    data class Success(val userId: String) : UpdateUserResult()
    sealed class Failure : UpdateUserResult() {
        data class UserNotFound(val userId: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
} 