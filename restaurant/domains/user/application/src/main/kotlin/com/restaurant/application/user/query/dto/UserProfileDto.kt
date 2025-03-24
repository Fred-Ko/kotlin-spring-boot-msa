package com.restaurant.application.user.query.dto

import java.time.LocalDateTime

data class UserProfileDto(
        val id: Long,
        val email: String,
        val name: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)
