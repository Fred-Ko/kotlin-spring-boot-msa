package com.restaurant.presentation.account.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "에러 응답 - 리소스를 찾을 수 없음")
data class NotFoundErrorResponse(
    @field:Schema(description = "문제 유형 URI", example = "probs/account_not_found")
    val type: String,
    @field:Schema(description = "에러 제목", example = "Account Not Found")
    val title: String,
    @field:Schema(description = "HTTP 상태 코드", example = "404")
    val status: Int,
    @field:Schema(description = "상세 메시지", example = "계좌를 찾을 수 없습니다. (ID: 123)")
    val detail: String,
    @field:Schema(description = "에러 코드", example = "ACCOUNT_NOT_FOUND")
    val errorCode: String,
    @field:Schema(description = "발생 시각", example = "2024-04-16T15:17:11.123Z")
    val timestamp: Instant,
)
