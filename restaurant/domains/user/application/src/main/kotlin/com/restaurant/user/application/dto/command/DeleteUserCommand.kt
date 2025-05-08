package com.restaurant.user.application.dto.command

import jakarta.validation.constraints.NotBlank

data class DeleteUserCommand(
    val userId: String,
    val reason: String? = null,
    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    val password: String,
)
