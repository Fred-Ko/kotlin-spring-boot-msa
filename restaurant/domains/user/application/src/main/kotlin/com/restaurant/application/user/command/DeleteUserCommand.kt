package com.restaurant.application.user.command

data class DeleteUserCommand(
    val userId: String,
    val password: String,
)
