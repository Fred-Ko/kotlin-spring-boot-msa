package com.ddd.user.application.command.result

sealed class CreateUserResult {
    data class Success(val userId: String) : CreateUserResult()
    sealed class Failure : CreateUserResult() {
        data class EmailAlreadyExists(val email: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
}
