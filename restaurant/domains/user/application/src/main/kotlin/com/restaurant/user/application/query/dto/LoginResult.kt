package com.restaurant.user.application.query.dto

/**
 * 로그인 결과 DTO (Application Layer)
 */
data class LoginResult(
    val userId: String,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
)
