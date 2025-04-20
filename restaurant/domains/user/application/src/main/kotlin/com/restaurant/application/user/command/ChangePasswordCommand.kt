package com.restaurant.application.user.command

data class ChangePasswordCommand(
    val userId: String,
    val currentPassword: String,
    val newPassword: String,
)
