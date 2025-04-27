package com.restaurant.user.application.dto.command

data class ChangePasswordCommand(
    val userId: String,
    val currentPassword: String,
    val newPassword: String,
)
