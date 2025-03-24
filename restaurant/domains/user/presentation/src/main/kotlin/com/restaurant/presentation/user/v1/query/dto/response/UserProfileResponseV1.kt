package com.restaurant.presentation.user.v1.query.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "사용자 프로필 응답")
data class UserProfileResponseV1(
        @field:Schema(description = "사용자 ID", example = "1") val id: Long,
        @field:Schema(description = "사용자 이메일", example = "user@example.com") val email: String,
        @field:Schema(description = "사용자 이름", example = "홍길동") val name: String,
        @field:Schema(description = "계정 생성 시간", example = "2023-01-01T12:00:00")
        val createdAt: LocalDateTime,
        @field:Schema(description = "계정 최종 수정 시간", example = "2023-01-01T12:00:00")
        val updatedAt: LocalDateTime
)
