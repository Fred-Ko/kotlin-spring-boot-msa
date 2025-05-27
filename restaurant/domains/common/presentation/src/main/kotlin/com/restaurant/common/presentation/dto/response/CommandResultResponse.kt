package com.restaurant.common.presentation.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Command 요청 결과 응답")
data class CommandResultResponse(
    @Schema(description = "처리 상태", example = "SUCCESS")
    val status: String,
    @Schema(description = "결과 메시지", example = "요청이 성공적으로 처리되었습니다.")
    val message: String,
    @Schema(description = "생성/수정된 리소스의 ID (선택적)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef", nullable = true)
    val resourceId: String? = null // 필요에 따라 추가
)
