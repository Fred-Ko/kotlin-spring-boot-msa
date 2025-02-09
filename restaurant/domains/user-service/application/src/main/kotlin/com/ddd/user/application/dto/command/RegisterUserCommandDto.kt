package com.ddd.user.application.dto.command

data class RegisterUserCommandDto(
        val email: String,
        val password: String,
        val name: String,
        val phoneNumber: String,
        val street: String,
        val city: String,
        val state: String,
        val zipCode: String,
)
