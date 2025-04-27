package com.restaurant.user.application.dto.command

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Email as JakartaEmail // Alias to avoid conflict

/**
 * Command DTO for registering a new user.
 * Rule App-Struct (dto/command)
 */
@Schema(description = "사용자 등록 요청 커맨드")
data class RegisterUserCommand(
    @Schema(description = "사용자 이메일", example = "test@example.com")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:JakartaEmail(message = "유효한 이메일 형식이 아닙니다.")
    val email: String,
    @Schema(description = "비밀번호", example = "password123!")
    @field:NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.") // Add password complexity rules if needed
    val password: String,
    @Schema(description = "사용자 이름", example = "홍길동")
    @field:NotBlank(message = "이름은 필수 입력 항목입니다.")
    val name: String,
    @Schema(description = "사용자 아이디", example = "testuser123") // Rule 6.3
    @field:NotBlank(message = "사용자 아이디는 필수 입력 항목입니다.")
    @field:Size(min = 3, max = 20, message = "사용자 아이디는 3자 이상 20자 이하이어야 합니다.")
    val username: String, // Rule 6.3: Added username field
)
