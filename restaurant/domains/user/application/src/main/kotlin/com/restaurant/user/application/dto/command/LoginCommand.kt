package com.restaurant.user.application.dto.command

data class LoginCommand(
    val email: String,
    val password: String,
)
