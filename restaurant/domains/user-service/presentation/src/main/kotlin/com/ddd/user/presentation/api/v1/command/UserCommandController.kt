package com.ddd.user.presentation.api.v1.command

import com.ddd.user.application.command.command.CreateUserCommand
import com.ddd.user.application.command.command.DeleteUserCommand
import com.ddd.user.application.command.command.UpdateUserCommand
import com.ddd.user.application.command.result.CreateUserResult
import com.ddd.user.application.command.result.DeleteUserResult
import com.ddd.user.application.command.result.UpdateUserResult
import com.ddd.user.application.command.usecase.CreateUserUseCase
import com.ddd.user.application.command.usecase.DeleteUserUseCase
import com.ddd.user.application.command.usecase.UpdateUserUseCase
import com.ddd.user.presentation.api.v1.command.dto.request.CreateUserRequest
import com.ddd.user.presentation.api.v1.command.dto.request.UpdateUserRequest
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserCommandController(
        private val createUserUseCase: CreateUserUseCase,
        private val updateUserUseCase: UpdateUserUseCase,
        private val deleteUserUseCase: DeleteUserUseCase
) {
        @PostMapping
        fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<String> {
                val command =
                        CreateUserCommand(
                                email = request.email,
                                password = request.password,
                                name = CreateUserCommand.Name(request.name),
                                phoneNumber = CreateUserCommand.PhoneNumber(request.phoneNumber),
                                address =
                                        CreateUserCommand.Address(
                                                request.address.street,
                                                request.address.city,
                                                request.address.state,
                                                request.address.zipCode
                                        )
                        )

                return when (val result = createUserUseCase.execute(command)) {
                        is CreateUserResult.Success -> ResponseEntity.ok(result.userId)
                        is CreateUserResult.Failure.EmailAlreadyExists ->
                                ResponseEntity.badRequest()
                                        .body("Email already exists: ${result.email}")
                        is CreateUserResult.Failure.ValidationError ->
                                ResponseEntity.badRequest()
                                        .body("Validation error: ${result.message}")
                }
        }

        @PutMapping("/{id}")
        fun updateUser(
                @PathVariable id: String,
                @RequestBody request: UpdateUserRequest
        ): ResponseEntity<String> {
                val command =
                        UpdateUserCommand(
                                id = UUID.fromString(id),
                                email = request.email,
                                password = request.password,
                                name = request.name,
                                phoneNumber = request.phoneNumber,
                                address =
                                        request.address?.let {
                                                UpdateUserCommand.Address(
                                                        it.street,
                                                        it.city,
                                                        it.state,
                                                        it.zipCode
                                                )
                                        }
                        )
                return when (val result = updateUserUseCase.execute(command)) {
                        is UpdateUserResult.Success -> ResponseEntity.ok(result.userId)
                        is UpdateUserResult.Failure.UserNotFound ->
                                ResponseEntity.badRequest().body("User not found: ${result.userId}")
                        is UpdateUserResult.Failure.ValidationError ->
                                ResponseEntity.badRequest()
                                        .body("Validation error: ${result.message}")
                }
        }

        @DeleteMapping("/{id}")
        fun deleteUser(@PathVariable id: String): ResponseEntity<String> {
                val command = DeleteUserCommand(UUID.fromString(id))
                return when (val result = deleteUserUseCase.execute(command)) {
                        is DeleteUserResult.Success -> ResponseEntity.ok(result.userId)
                        is DeleteUserResult.Failure.UserNotFound ->
                                ResponseEntity.badRequest().body("User not found: ${result.userId}")
                        is DeleteUserResult.Failure.ValidationError ->
                                ResponseEntity.badRequest()
                                        .body("Validation error: ${result.message}")
                }
        }
}
