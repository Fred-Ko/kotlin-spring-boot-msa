package com.restaurant.presentation.account.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "에러 응답 - 비즈니스 규칙 위반")
data class BusinessErrorResponse(
    @field:Schema(description = "문제 유형 URI", example = "probs/insufficient_balance")
    val type: String,
    @field:Schema(description = "에러 제목", example = "Insufficient Balance")
    val title: String,
    @field:Schema(description = "HTTP 상태 코드", example = "400")
    val status: Int,
    @field:Schema(description = "상세 메시지", example = "잔액이 부족합니다.")
    val detail: String,
    @field:Schema(description = "에러 코드", example = "INSUFFICIENT_BALANCE")
    val errorCode: String,
    @field:Schema(description = "발생 시각", example = "2024-04-16T15:17:11.123Z")
    val timestamp: Instant,
)
