package com.restaurant.common.presentation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Simple", description = "간단한 테스트 API")
@RestController
@RequestMapping("/api/v1/simple")
class SimpleController {
    @GetMapping("/test")
    @Operation(summary = "테스트", description = "간단한 테스트 엔드포인트")
    @ApiResponse(
        responseCode = "200",
        description = "테스트 성공",
        content = [
            Content(
                mediaType = "text/plain",
                schema =
                    Schema(
                        type = "string",
                        example = "Hello Swagger!",
                    ),
            ),
        ],
    )
    fun test(): String = "Hello Swagger!"
}
