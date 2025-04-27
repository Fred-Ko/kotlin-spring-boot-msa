package com.restaurant.user.application.dto.command

data class UpdateProfileCommand(
    val userId: String,
    val name: String,
    val phoneNumber: String?,
)
