package com.ddd.user.application.dto.command

import java.util.UUID

data class ChangePasswordCommandDto(
        val id: UUID,
        val currentPassword: String,
        val newPassword: String,
)
