package com.ddd.user.application.query.result

import com.ddd.user.application.query.dto.UserDto

sealed class GetUserResult {
    data class Success(val user: UserDto) : GetUserResult()
    sealed class Failure : GetUserResult() {
        data class UserNotFound(val userId: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
}
