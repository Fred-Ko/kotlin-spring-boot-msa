package com.restaurant.application.user.command

data class RegisterUserCommand(val email: String, val password: String, val name: String)
