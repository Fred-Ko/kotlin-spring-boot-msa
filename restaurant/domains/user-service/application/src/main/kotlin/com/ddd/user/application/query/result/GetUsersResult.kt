package com.ddd.user.application.query.result

import com.ddd.user.application.query.dto.UserDto
import org.springframework.data.domain.Page

sealed class GetUsersResult {
    data class Success(val usersPage: Page<UserDto>) : GetUsersResult()
    sealed class Failure : GetUsersResult() {
        data class ValidationError(val message: String) : Failure()
    }
}
