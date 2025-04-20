package com.restaurant.application.user.command

data class UpdateProfileCommand(
    val userId: String,
    val name: String,
)
