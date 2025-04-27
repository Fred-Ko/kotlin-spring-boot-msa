package com.restaurant.config.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

/**
 * Common response DTO for successful command executions.
 * Includes status, message, correlationId, and HATEOAS links.
 * Rule 35, 39
 */
@Schema(description = "커맨드 처리 결과 응답")
@JsonInclude(JsonInclude.Include.NON_NULL) // Null 필드는 제외
data class CommandResultResponse(
    @Schema(description = "처리 상태", example = "SUCCESS", defaultValue = "SUCCESS")
    val status: String = "SUCCESS",
    @Schema(description = "결과 메시지", example = "작업이 성공적으로 완료되었습니다.")
    val message: String,
    @Schema(description = "생성/수정된 리소스 ID (선택 사항)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
    val resourceId: String? = null,
    @Schema(description = "요청 추적 ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    val correlationId: String,
    @Schema(description = "비동기 작업 ID (선택 사항)", example = "job-12345", nullable = true)
    val jobId: String? = null // Rule 36 (비동기 응답 시 사용)
) : RepresentationModel<CommandResultResponse>() // Rule 39: HATEOAS 지원
