package com.restaurant.user.application.command.dto

data class UpdateProfileCommand(
    val userId: String,
    val name: String,
    val phoneNumber: String?,
)
