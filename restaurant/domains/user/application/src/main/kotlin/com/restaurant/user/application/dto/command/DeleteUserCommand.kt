package com.restaurant.user.application.dto.command

import jakarta.validation.constraints.NotBlank

// Application 레이어의 Command DTO (Rule App-Struct)
data class DeleteUserCommand(
    // 삭제할 사용자 ID
    val userId: String,
    // 삭제 사유(선택)
    val reason: String? = null,
    // 비밀번호 확인
    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    val password: String,
)
