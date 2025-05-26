package com.restaurant.common.presentation.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "명령 실행 결과 응답")
data class CommandResultResponse(
    @Schema(description = "실행 결과 상태", example = "SUCCESS", defaultValue = "SUCCESS", allowableValues = ["SUCCESS", "FAILURE"])
    val status: String = "SUCCESS",
    @Schema(description = "결과 메시지 (성공 또는 실패 상세 내용)", example = "User registered successfully.")
    val message: String? = null,
    @Schema(description = "관련 리소스 ID (선택 사항)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", nullable = true)
    val resourceId: String? = null,
)
