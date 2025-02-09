package com.ddd.user.presentation.api.v1.command

import com.ddd.user.application.command.*
import com.ddd.user.application.command.ChangePasswordCommand
import com.ddd.user.application.dto.command.*
import com.ddd.user.presentation.api.v1.command.dto.request.*
import com.ddd.user.presentation.api.v1.command.dto.response.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "User Command API", description = "User Command API 입니다.")
@RestController
@RequestMapping("/api/v1/users")
class UserCommandController(
        private val registerUserUseCase: RegisterUserCommand,
        private val modifyUserUseCase: ModifyUserCommand,
        private val changePasswordUseCase: ChangePasswordCommand,
        private val deactivateUserUseCase: DeactivateUserCommand,
        private val deleteUserUseCase: DeleteUserCommand,
) {
        @Operation(summary = "User 등록", description = "User 를 등록합니다.")
        @PostMapping
        fun registerUser(
                @Valid @RequestBody request: RegisterUserRequest
        ): ResponseEntity<UserResponse> {
                val command =
                        RegisterUserCommandDto(
                                email = request.email,
                                password = request.password,
                                name = request.name,
                                phoneNumber = request.phoneNumber,
                                street = request.street,
                                city = request.city,
                                state = request.state,
                                zipCode = request.zipCode,
                        )
                registerUserUseCase.registerUser(command)
                return ResponseEntity.ok(UserResponse("User registered successfully"))
        }

        @Operation(summary = "User 수정", description = "User 를 수정합니다.")
        @PutMapping("/{id}")
        fun modifyUser(
                @PathVariable id: String,
                @Valid @RequestBody request: ModifyUserRequest,
        ): ResponseEntity<UserResponse> {
                val command =
                        ModifyUserCommandDto(
                                id = id,
                                name = request.name,
                                phoneNumber = request.phoneNumber,
                                street = request.street,
                                city = request.city,
                                state = request.state,
                                zipCode = request.zipCode,
                        )
                modifyUserUseCase.modifyUser(command)
                return ResponseEntity.ok(UserResponse("User modified successfully"))
        }

        @Operation(summary = "User 비밀번호 변경", description = "User 비밀번호를 변경합니다.")
        @PutMapping("/{id}/password")
        fun changePassword(
                @PathVariable id: String,
                @Valid @RequestBody request: ChangePasswordRequest,
        ): ResponseEntity<UserResponse> {
                val command =
                        ChangePasswordCommandDto(
                                id = id,
                                currentPassword = request.currentPassword,
                                newPassword = request.newPassword,
                        )
                changePasswordUseCase.changePassword(command)
                return ResponseEntity.ok(UserResponse("Password changed successfully"))
        }

        @Operation(summary = "User 삭제", description = "User 를 삭제합니다.")
        @DeleteMapping("/{id}")
        fun deleteUser(@PathVariable id: String): ResponseEntity<UserResponse> {
                val command =
                        DeleteUserCommandDto(
                                id = id,
                        )
                deleteUserUseCase.deleteUser(command)
                return ResponseEntity.ok(UserResponse("User deleted successfully"))
        }

        @Operation(summary = "User 비활성화", description = "User 를 비활성화합니다.")
        @PutMapping("/{id}/deactivate")
        fun deactivateUser(@PathVariable id: String): ResponseEntity<UserResponse> {
                val command =
                        DeactivateUserCommandDto(
                                id = id,
                        )
                deactivateUserUseCase.deactivateUser(command)
                return ResponseEntity.ok(UserResponse("User deactivated successfully"))
        }
}
