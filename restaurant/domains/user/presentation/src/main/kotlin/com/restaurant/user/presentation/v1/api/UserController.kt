package com.restaurant.user.presentation.v1.api

import com.restaurant.common.config.dto.response.CommandResultResponse
import com.restaurant.common.config.filter.CorrelationIdFilter
// Command Use Cases
import com.restaurant.user.application.port.`in`.ChangePasswordUseCase
import com.restaurant.user.application.port.`in`.DeleteAddressUseCase
import com.restaurant.user.application.port.`in`.DeleteUserUseCase
import com.restaurant.user.application.port.`in`.RegisterAddressUseCase
import com.restaurant.user.application.port.`in`.RegisterUserUseCase
import com.restaurant.user.application.port.`in`.UpdateAddressUseCase
import com.restaurant.user.application.port.`in`.UpdateProfileUseCase
// Query Use Cases
import com.restaurant.user.application.port.`in`.GetUserAddressesQuery
import com.restaurant.user.application.port.`in`.GetUserProfileQuery
// DTOs
import com.restaurant.user.application.dto.command.DeleteAddressCommand
import com.restaurant.user.application.dto.query.AddressDto
import com.restaurant.user.application.dto.query.GetUserAddressesQuery as AppGetUserAddressesQuery
import com.restaurant.user.application.dto.query.GetUserProfileByIdQuery
import com.restaurant.user.application.dto.query.UserProfileDto
import com.restaurant.user.presentation.v1.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateProfileRequestV1
import com.restaurant.user.presentation.v1.dto.response.AddressResponseV1
import com.restaurant.user.presentation.v1.dto.response.UserProfileResponseV1
// Extensions
import com.restaurant.user.presentation.v1.extensions.request.toCommand
import com.restaurant.user.presentation.v1.extensions.response.toResponse
import com.restaurant.user.presentation.v1.extensions.response.toResponseV1
// Other imports
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.MDC
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

private val log = KotlinLogging.logger {}

@Tag(name = "User Commands", description = "API for user account management (registration, login, profile updates, password change, deletion)")
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
    fun registerUser(@Valid @RequestBody request: RegisterUserRequestV1): ResponseEntity<CommandResultResponse> {
        log.info("Register user request received: {}", request.username)
        val command = request.toCommand()
        val userId = registerUserUseCase.handle(command)
        val response = CommandResultResponse(
            status = "SUCCESS",
            message = "User registered successfully",
        ).apply {
            add(linkTo(methodOn(UserQueryController::class.java).getUserProfile(userId.value.toString())).withSelfRel())
            add(linkTo(methodOn(UserController::class.java).loginUser(null)).withRel("login")) // Pass null for request body placeholder
        }
        return ResponseEntity.created(response.getRequiredLink("self").toUri()).body(response)
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    @ApiResponse(responseCode = "200", description = "User logged in successfully")
    fun loginUser(@Valid @RequestBody request: LoginRequestV1): ResponseEntity<Any> { // Return type can be more specific
        log.info("Login request received for email: {}", request.email)
        val command = request.toCommand()
        val loginResult = loginUseCase.login(command)
        // Login response usually includes the token directly, maybe not CommandResultResponse
        // HATEOAS for login might link to profile, logout etc.
        val response = mapOf(
             "status" to "SUCCESS",
             "message" to "Login successful",
             "data" to loginResult
        ) // Example - Adapt response as needed
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update user profile")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    fun updateProfile(
        @Parameter(description = "User ID") @PathVariable userId: UUID,
        @Valid @RequestBody request: UpdateProfileRequestV1
    ): ResponseEntity<CommandResultResponse> {
        log.info("Update profile request received for user: {}", userId)
        val command = request.toCommand(userId)
        updateProfileUseCase.handle(command)
        val response = CommandResultResponse(
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
        @Valid @RequestBody request: ChangePasswordRequestV1
    ): ResponseEntity<CommandResultResponse> {
        log.info("Change password request received for user: {}", userId)
        val command = request.toCommand(userId)
        changePasswordUseCase.handle(command)
        val response = CommandResultResponse(
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
        @Valid @RequestBody request: DeleteUserRequestV1 // Assuming password needed for deletion
    ): ResponseEntity<CommandResultResponse> {
        log.info("Delete user request received for user: {}", userId)
        val command = request.toCommand(userId)
        deleteUserUseCase.handle(command)
        val response = CommandResultResponse(
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
