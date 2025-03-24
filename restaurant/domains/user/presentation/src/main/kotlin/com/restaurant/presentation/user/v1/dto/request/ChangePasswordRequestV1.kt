package com.restaurant.presentation.user.v1.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordRequestV1(
        @field:NotBlank(message = "현재 비밀번호는 필수입니다.") val currentPassword: String,
        @field:NotBlank(message = "새 비밀번호는 필수입니다.")
        @field:Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
        val newPassword: String
)
