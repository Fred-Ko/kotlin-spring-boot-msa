package com.ddd.user.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class RegisterUserRequest(
        @field:Email @field:NotBlank val email: String,
        @field:NotBlank @field:Size(min = 8, max = 20) val password: String,
        @field:NotBlank val name: String,
        @field:NotBlank val phoneNumber: String,
        @field:NotBlank val street: String,
        @field:NotBlank val city: String,
        @field:NotBlank val state: String,
        @field:NotBlank val zipCode: String,
)

data class ModifyUserRequest(
        val id: UUID,
        @field:NotBlank val name: String,
        @field:NotBlank val phoneNumber: String,
        @field:NotBlank val street: String,
        @field:NotBlank val city: String,
        @field:NotBlank val state: String,
        @field:NotBlank val zipCode: String,
)

data class ChangePasswordRequest(
        val id: UUID,
        @field:NotBlank val currentPassword: String,
        @field:NotBlank @field:Size(min = 8, max = 20) val newPassword: String,
)

data class DeleteUserRequest(
        val id: UUID,
)

data class DeactivateUserRequest(
        val id: UUID,
)
