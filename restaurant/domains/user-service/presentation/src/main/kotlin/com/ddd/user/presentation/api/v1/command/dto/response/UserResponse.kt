package com.ddd.user.presentation.api.v1.command.dto.response

import com.ddd.user.application.query.dto.UserDto
import com.ddd.user.application.query.result.GetUserResult
import com.ddd.user.application.query.result.GetUsersResult
import org.springframework.data.domain.Page

data class UserResponse(
        val id: String,
        val email: String,
        val name: String,
) {
    companion object {
        fun from(result: GetUserResult.Success) =
                UserResponse(
                        id = result.user.id.toString(),
                        email = result.user.email,
                        name = result.user.name,
                )

        fun from(userDto: UserDto) =
                UserResponse(
                        id = userDto.id.toString(),
                        email = userDto.email,
                        name = userDto.name,
                )
    }
}

data class UsersResponse(val users: Page<UserResponse>) {
    companion object {
        fun from(result: GetUsersResult.Success) =
                UsersResponse(users = result.usersPage.map { UserResponse.from(it) })
    }
}
