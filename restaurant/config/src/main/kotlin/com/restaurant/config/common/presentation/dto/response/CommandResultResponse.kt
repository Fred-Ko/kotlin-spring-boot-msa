package com.restaurant.config.common.presentation.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel

@Schema(description = "기본 Command 처리 결과 응답")
data class CommandResultResponse(
    @Schema(description = "처리 상태 (예: SUCCESS)", example = "SUCCESS")
    val status: String = "SUCCESS",
    @Schema(description = "결과 메시지", example = "Operation completed successfully")
    val message: String,
    @Schema(description = "요청 추적 ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    val correlationId: String,
    // Optional ID of the created/affected resource
    @Schema(description = "생성/영향받은 리소스 ID (선택적)", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8", nullable = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val resourceId: String? = null,
) : RepresentationModel<CommandResultResponse>() { // Rule 39: Extend RepresentationModel for HATEOAS

    // AddLink is automatically provided by RepresentationModel
    // Ensure _links property is serialized correctly (usually handled by Spring HATEOAS + Jackson)
}
