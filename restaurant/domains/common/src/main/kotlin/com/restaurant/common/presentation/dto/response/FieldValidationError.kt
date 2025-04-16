package com.restaurant.common.presentation.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "필드별 유효성 검사 에러 정보")
data class FieldValidationError(
    @field:Schema(description = "필드명", example = "amount")
    val field: String,
    @field:Schema(description = "에러 메시지", example = "must be greater than 0")
    val message: String,
    @field:Schema(description = "거절된 값", example = "-1000")
    val rejectedValue: Any?,
)
