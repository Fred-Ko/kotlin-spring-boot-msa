package com.restaurant.config.presentation.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.slf4j.MDC
import org.springframework.hateoas.RepresentationModel

@Schema(description = "커맨드 실행 결과 응답")
data class CommandResultResponse(
    @field:Schema(description = "처리 상태", example = "SUCCESS", required = true)
    val status: String,
    @field:Schema(description = "결과 메시지", example = "처리가 성공적으로 완료되었습니다.", required = true)
    val message: String,
    @field:Schema(description = "상관 관계 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    val correlationId: String = MDC.get("correlationId") ?: "UNKNOWN",
) : RepresentationModel<CommandResultResponse>()
