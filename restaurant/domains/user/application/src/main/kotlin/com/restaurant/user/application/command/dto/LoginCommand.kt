package com.restaurant.user.application.command.dto

data class LoginCommand(
    val email: String,
    val password: String,
)
