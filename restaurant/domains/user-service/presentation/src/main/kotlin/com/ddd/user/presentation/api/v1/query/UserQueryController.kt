package com.ddd.user.presentation.api.v1.query

import com.ddd.user.application.query.query.GetUserQuery
import com.ddd.user.application.query.query.GetUsersQuery
import com.ddd.user.application.query.result.GetUserResult
import com.ddd.user.application.query.result.GetUsersResult
import com.ddd.user.application.query.usecase.GetUserUseCase
import com.ddd.user.application.query.usecase.GetUsersUseCase
import com.ddd.user.presentation.api.v1.command.dto.response.UserResponse
import com.ddd.user.presentation.api.v1.command.dto.response.UsersResponse
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserQueryController(
        private val getUserUseCase: GetUserUseCase,
        private val getUsersUseCase: GetUsersUseCase
) {
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<UserResponse> {
        return when (val result = getUserUseCase.execute(GetUserQuery(UUID.fromString(id)))) {
            is GetUserResult.Success -> ResponseEntity.ok(UserResponse.from(result))
            is GetUserResult.Failure.UserNotFound -> ResponseEntity.badRequest().build()
            is GetUserResult.Failure.ValidationError -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping
    fun getUsers(
            @RequestParam(required = false, defaultValue = "0") page: Int,
            @RequestParam(required = false, defaultValue = "10") size: Int
    ): ResponseEntity<UsersResponse> {
        return when (val result = getUsersUseCase.execute(GetUsersQuery(page = page, size = size))
        ) {
            is GetUsersResult.Success -> ResponseEntity.ok(UsersResponse.from(result))
            is GetUsersResult.Failure.ValidationError -> ResponseEntity.badRequest().build()
        }
    }
}
