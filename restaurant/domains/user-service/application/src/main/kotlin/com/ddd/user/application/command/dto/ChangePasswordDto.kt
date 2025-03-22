package com.ddd.user.application.dto.command

import java.util.UUID

data class ChangePasswordDto(
        val id: UUID,
        val currentPassword: String,
        val newPassword: String,
)
