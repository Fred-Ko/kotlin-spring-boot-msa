package com.restaurant.user.presentation.v1.command.controller

import com.restaurant.user.presentation.v1.command.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.LoginRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateProfileRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.DeleteUserRequestV1

import com.restaurant.user.presentation.v1.command.extensions.dto.request.toCommand

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.user.application.port.ChangePasswordUseCase
import com.restaurant.user.application.port.DeleteUserUseCase
import com.restaurant.user.application.port.LoginUseCase
import com.restaurant.user.application.port.RegisterUserUseCase
import com.restaurant.user.application.port.UpdateProfileUseCase
import com.restaurant.user.domain.vo.UserId

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

private val log = KotlinLogging.logger {}

@Tag(
    name = "User Commands",
    description = "API for user account management (registration, login, profile updates, password change, deletion)",
)
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
    // private val userProfileImageUseCase: UserProfileImageUseCase // 프로필 이미지 기능은 추후 구현
) {
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    fun registerUser(
        @Valid @RequestBody request: RegisterUserRequestV1,
        @RequestHeader("X-Correlation-Id") correlationId: String
    ): ResponseEntity<CommandResultResponse> {
        log.info { "[Correlation ID: $correlationId] Received request to register user: ${request.username}" }
        val command = request.toCommand(correlationId)
        val userId: UserId = registerUserUseCase.register(command)

        val location = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/v1/users/{userId}/profile")
            .buildAndExpand(userId.value)
            .toUri()

        return ResponseEntity.created(location).body(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User registered successfully.",
                correlationId = correlationId
            )
        )
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    fun login(
        @Valid @RequestBody request: LoginRequestV1,
        @RequestHeader("X-Correlation-Id") correlationId: String
    ): ResponseEntity<CommandResultResponse> {
        log.info { "[Correlation ID: $correlationId] Received login request for user: ${request.email}" }
        val command = request.toCommand(correlationId)
        val loginResult = loginUseCase.login(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Login successful. Token: ${loginResult.accessToken}",
                correlationId = correlationId
            )
        )
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update user profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    fun updateProfile(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateProfileRequestV1,
        @RequestHeader("X-Correlation-Id") correlationId: String
    ): ResponseEntity<CommandResultResponse> {
        log.info { "[Correlation ID: $correlationId] Received request to update profile for user ID: $userId" }
        val command = request.toCommand(UserId.of(userId), correlationId)
        updateProfileUseCase.updateProfile(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User profile updated successfully.",
                correlationId = correlationId
            )
        )
    }

    @PatchMapping("/{userId}/password")
    @Operation(summary = "Change user password")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    fun changePassword(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: ChangePasswordRequestV1,
        @RequestHeader("X-Correlation-Id") correlationId: String
    ): ResponseEntity<CommandResultResponse> {
        log.info { "[Correlation ID: $correlationId] Received request to change password for user ID: $userId" }
        val command = request.toCommand(UserId.of(userId), correlationId)
        changePasswordUseCase.changePassword(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User password changed successfully.",
                correlationId = correlationId
            )
        )
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user account")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    fun deleteUser(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: DeleteUserRequestV1,
        @RequestHeader("X-Correlation-Id") correlationId: String
    ): ResponseEntity<CommandResultResponse> {
        log.info { "[Correlation ID: $correlationId] Received request to delete user ID: $userId" }
        val command = request.toCommand(UserId.of(userId), correlationId)
        deleteUserUseCase.deleteUser(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User deleted successfully.",
                correlationId = correlationId
            )
        )
    }
}
