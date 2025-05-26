package com.restaurant.user.application.command.dto

data class ChangePasswordCommand(
    val userId: String,
    val currentPassword: String,
    val newPassword: String,
)
