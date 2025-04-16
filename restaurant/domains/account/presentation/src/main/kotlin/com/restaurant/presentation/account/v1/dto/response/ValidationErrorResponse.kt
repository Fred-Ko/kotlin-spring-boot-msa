package com.restaurant.presentation.account.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "에러 응답 - 유효성 검사 실패")
data class ValidationErrorResponse(
    @field:Schema(description = "문제 유형 URI", example = "probs/validation_error")
    val type: String,
    @field:Schema(description = "에러 제목", example = "Validation Error")
    val title: String,
    @field:Schema(description = "HTTP 상태 코드", example = "400")
    val status: Int,
    @field:Schema(description = "상세 메시지", example = "입력값 유효성 검사에 실패했습니다.")
    val detail: String,
    @field:Schema(description = "에러 코드", example = "VALIDATION_ERROR")
    val errorCode: String,
    @field:Schema(description = "발생 시각", example = "2024-04-16T15:17:11.123Z")
    val timestamp: Instant,
    @field:Schema(description = "필드별 에러 정보")
    val errors: List<FieldValidationError>,
)
