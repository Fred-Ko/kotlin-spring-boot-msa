package com.ddd.user.application.command.result

sealed class DeleteUserResult {
    data class Success(val userId: String) : DeleteUserResult()
    sealed class Failure : DeleteUserResult() {
        data class UserNotFound(val userId: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
}
