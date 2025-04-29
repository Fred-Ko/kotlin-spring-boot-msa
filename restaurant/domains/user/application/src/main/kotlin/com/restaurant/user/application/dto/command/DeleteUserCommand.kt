package com.restaurant.user.application.dto.command

import jakarta.validation.constraints.NotBlank

// Application 레이어의 Command DTO (Rule App-Struct)
data class DeleteUserCommand(
    val userId: String, // UUID String
    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    val password: String, // 수정: passwordConfirmation -> password
)
