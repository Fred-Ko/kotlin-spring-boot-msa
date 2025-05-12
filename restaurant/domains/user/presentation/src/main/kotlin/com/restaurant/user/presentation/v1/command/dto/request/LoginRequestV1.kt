package com.restaurant.user.presentation.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestV1(
    @field:Schema(description = "사용자 이메일", example = "user@example.com")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Email(message = "유효한 이메일 형식이 아닙니다.")
    val email: String,
    @field:Schema(description = "사용자 비밀번호", example = "password123")
    @field:NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    val password: String,
)
