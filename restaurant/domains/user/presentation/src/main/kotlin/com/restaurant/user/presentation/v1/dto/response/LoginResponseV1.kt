package com.restaurant.user.presentation.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(description = "로그인 응답 V1")
data class LoginResponseV1(
    @Schema(description = "사용자 ID (UUID)")
    val userId: String,
    @Schema(description = "사용자 이름")
    val username: String,
    @Schema(description = "액세스 토큰")
    val accessToken: String,
    @Schema(description = "리프레시 토큰")
    val refreshToken: String,
) : RepresentationModel<LoginResponseV1>()
