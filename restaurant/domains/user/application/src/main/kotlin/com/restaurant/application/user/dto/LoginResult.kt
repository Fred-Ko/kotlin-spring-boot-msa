package com.restaurant.application.user.dto

import java.util.UUID

// 로그인 결과를 담는 데이터 클래스
data class LoginResult(
    val userId: String,
    val accessToken: String = UUID.randomUUID().toString(), // 임시 구현
    val refreshToken: String = UUID.randomUUID().toString(), // 임시 구현
)
