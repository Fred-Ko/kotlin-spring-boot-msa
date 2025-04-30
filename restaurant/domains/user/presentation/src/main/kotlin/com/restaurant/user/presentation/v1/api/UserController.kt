package com.restaurant.user.presentation.v1.api

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.user.application.dto.command.ChangePasswordCommand
import com.restaurant.user.application.dto.command.DeleteUserCommand
import com.restaurant.user.application.dto.command.LoginCommand
import com.restaurant.user.application.dto.command.RegisterUserCommand
import com.restaurant.user.application.dto.command.UpdateProfileCommand
import com.restaurant.user.application.port.input.ChangePasswordUseCase
import com.restaurant.user.application.port.input.DeleteUserUseCase
import com.restaurant.user.application.port.input.LoginUseCase
import com.restaurant.user.application.port.input.RegisterUserUseCase
import com.restaurant.user.application.port.input.UpdateProfileUseCase
// Query Use Cases
// DTOs
import com.restaurant.user.presentation.v1.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateProfileRequestV1
import com.restaurant.user.presentation.v1.dto.request.LoginRequestV1
// Extensions
import com.restaurant.user.presentation.v1.extensions.command.dto.request.toCommand
// Other imports
import mu.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
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
    private val deleteUserUseCase: DeleteUserUseCase,
    // Remove Address Use Cases - Moved to UserAddressController
    // private val registerAddressUseCase: RegisterAddressUseCase,
    // private val updateAddressUseCase: UpdateAddressUseCase,
    // private val deleteAddressUseCase: DeleteAddressUseCase
) {
    @PostMapping
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    fun registerUser(
        @Valid @RequestBody request: RegisterUserRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        log.info("Register user request received: {}", request.username)
        val command = request.toCommand()
        val userId = registerUserUseCase.register(command)
        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "User registered successfully",
            ).apply {
                add(linkTo(methodOn(UserQueryController::class.java).getUserProfile(userId.toString())).withSelfRel())
                add(linkTo(UserController::class.java).slash("login").withRel("login"))
            }
        return ResponseEntity.created(response.getRequiredLink("self").toUri()).body(response)
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    fun loginUser(
        @Valid @RequestBody request: LoginRequestV1,
    ): ResponseEntity<Any> { // Return type can be more specific
        log.info("Login request received for email: {}", request.email)
        val command = request.toCommand()
        val loginResult = loginUseCase.login(command)
        // Login response usually includes the token directly, maybe not CommandResultResponse
        // HATEOAS for login might link to profile, logout etc.
        val response =
            mapOf(
                "status" to "SUCCESS",
                "message" to "Login successful",
                "data" to loginResult,
            ) // Example - Adapt response as needed
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update user profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    fun updateProfile(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateProfileRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        log.info("Update profile request received for user: {}", userId)
        val command = request.toCommand(userId)
        updateProfileUseCase.handle(command)
        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "Profile updated successfully",
            ).apply {
                add(linkTo(methodOn(UserQueryController::class.java).getUserProfile(userId.toString())).withSelfRel())
            }
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "Change user password")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    fun changePassword(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: ChangePasswordRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        log.info("Change password request received for user: {}", userId)
        val command = request.toCommand(userId)
        changePasswordUseCase.handle(command)
        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "Password changed successfully",
            ).apply {
                add(linkTo(methodOn(UserQueryController::class.java).getUserProfile(userId.toString())).withRel("view-profile"))
            }
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user account")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    fun deleteUser(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: DeleteUserRequestV1, // Assuming password needed for deletion
    ): ResponseEntity<CommandResultResponse> {
        log.info("Delete user request received for user: {}", userId)
        val command = request.toCommand(userId)
        deleteUserUseCase.handle(command)
        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "User deleted successfully",
            )
        // No HATEOAS links usually for deletion confirmation
        return ResponseEntity.ok(response)
    }

    // Remove Address related endpoints
    // @PostMapping("/{userId}/addresses") ...
    // @PutMapping("/{userId}/addresses/{addressId}") ...
    // @DeleteMapping("/{userId}/addresses/{addressId}") ...

    // Remove Query related endpoints (moved to UserQueryController)
    // @GetMapping("/{userId}/profile") ...
    // @GetMapping("/{userId}/addresses") ...
}
