package com.restaurant.presentation.user.v1.command.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(description = "로그인 응답")
data class LoginResponseV1(
    @Schema(description = "상태", example = "SUCCESS") val status: String,
    @Schema(description = "메시지", example = "로그인 성공") val message: String,
    @Schema(description = "사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000") val userId: String,
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") val accessToken: String,
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") val refreshToken: String,
    @Schema(description = "상관 관계 ID", example = "correlationId-123") val correlationId: String,
) : RepresentationModel<LoginResponseV1>()
