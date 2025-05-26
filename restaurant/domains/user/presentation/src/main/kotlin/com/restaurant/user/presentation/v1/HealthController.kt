package com.restaurant.user.presentation.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Health Check", description = "애플리케이션 상태 확인 API")
@RestController
@RequestMapping("/api/v1/health")
class HealthController {
    @GetMapping
    @Operation(summary = "헬스 체크", description = "애플리케이션의 상태를 확인합니다.")
    @ApiResponse(
        responseCode = "200",
        description = "애플리케이션 정상 동작",
        content = [
            Content(
                mediaType = "application/json",
                schema =
                    Schema(
                        type = "object",
                        example = """{"status": "UP"}""",
                    ),
            ),
        ],
    )
    fun health(): Map<String, String> = mapOf("status" to "UP")
}
