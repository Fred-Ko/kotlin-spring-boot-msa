package com.restaurant.presentation.account.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "에러 응답 - 서버 내부 오류")
data class InternalServerErrorResponse(
    @field:Schema(description = "문제 유형 URI", example = "probs/internal_server_error")
    val type: String,
    @field:Schema(description = "에러 제목", example = "Internal Server Error")
    val title: String,
    @field:Schema(description = "HTTP 상태 코드", example = "500")
    val status: Int,
    @field:Schema(description = "상세 메시지", example = "서버 내부 오류가 발생했습니다.")
    val detail: String,
    @field:Schema(description = "에러 코드", example = "INTERNAL_SERVER_ERROR")
    val errorCode: String,
    @field:Schema(description = "발생 시각", example = "2024-04-16T15:17:11.123Z")
    val timestamp: Instant,
    @field:Schema(description = "예외 클래스명", example = "NullPointerException")
    val exception: String,
)
