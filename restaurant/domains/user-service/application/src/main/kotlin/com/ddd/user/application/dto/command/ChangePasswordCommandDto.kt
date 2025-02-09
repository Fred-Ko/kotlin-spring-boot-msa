package com.ddd.user.application.dto.command

data class ChangePasswordCommandDto(
        val id: String,
        val currentPassword: String,
        val newPassword: String,
)
