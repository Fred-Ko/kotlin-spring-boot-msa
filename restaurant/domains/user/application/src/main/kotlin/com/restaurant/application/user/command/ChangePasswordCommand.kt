package com.restaurant.application.user.command

data class ChangePasswordCommand(
        val userId: Long,
        val currentPassword: String,
        val newPassword: String
)
