package com.restaurant.user.application.dto.command

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

// Application 레이어의 Command DTO (Rule App-Struct)
@Schema(description = "사용자 삭제 요청 DTO")
data class DeleteUserCommand(
    @Schema(description = "삭제할 사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val userId: String, // UUID String
    @Schema(description = "계정 확인을 위한 현재 비밀번호", example = "currentpassword123")
    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    val password: String, // 수정: passwordConfirmation -> password
)
