package com.restaurant.user.presentation.v1.command.controller

import com.restaurant.user.presentation.v1.command.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.LoginRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateProfileRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.DeleteUserRequestV1

import com.restaurant.user.presentation.v1.command.extensions.dto.request.toCommand

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.user.application.dto.command.*
import com.restaurant.user.application.port.*
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
) {
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    fun registerUser(
        @Valid @RequestBody request: RegisterUserRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand()
        val userId: UserId = registerUserUseCase.register(command)

        val location = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/v1/users/{userId}/profile")
            .buildAndExpand(userId.value)
            .toUri()

        return ResponseEntity.created(location).body(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User registered successfully."
            )
        )
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    fun login(
        @Valid @RequestBody request: LoginRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand()
        val loginResult = loginUseCase.login(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Login successful. Token: ${loginResult.accessToken}"
            )
        )
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update user profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    fun updateProfile(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateProfileRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId))
        updateProfileUseCase.updateProfile(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User profile updated successfully."
            )
        )
    }

    @PatchMapping("/{userId}/password")
    @Operation(summary = "Change user password")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    fun changePassword(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: ChangePasswordRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId))
        changePasswordUseCase.changePassword(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User password changed successfully."
            )
        )
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user account")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    fun deleteUser(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: DeleteUserRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId))
        deleteUserUseCase.deleteUser(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User deleted successfully."
            )
        )
    }
}
