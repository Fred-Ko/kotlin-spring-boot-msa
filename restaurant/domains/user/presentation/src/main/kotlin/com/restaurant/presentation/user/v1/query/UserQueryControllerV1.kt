package com.restaurant.presentation.user.v1.query

import com.restaurant.application.user.handler.GetUserProfileQueryHandler
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.presentation.user.extensions.v1.response.toResponse
import com.restaurant.presentation.user.v1.query.dto.response.UserProfileResponseV1
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 조회", description = "사용자 프로필 조회 API")
class UserQueryControllerV1(
    private val getUserProfileQueryHandler: GetUserProfileQueryHandler,
) {
    private val log = LoggerFactory.getLogger(UserQueryControllerV1::class.java)

    @GetMapping("/{userId}")
    @Operation(summary = "사용자 프로필 조회", description = "사용자 ID를 통해 프로필 정보를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "프로필 조회 성공",
                content = [Content(mediaType = "application/json")],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [
                    Content(
                        mediaType = "application/problem+json",
                        schema =
                            io.swagger.v3.oas.annotations.media
                                .Schema(implementation = org.springframework.http.ProblemDetail::class),
                    ),
                ],
            ),
        ],
    )
    fun getUserProfile(
        @Parameter(description = "사용자 ID", required = true) @PathVariable userId: String,
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationId: String? = null,
    ): ResponseEntity<UserProfileResponseV1> {
        val finalCorrelationId =
            correlationId ?: java.util.UUID
                .randomUUID()
                .toString()
        log.debug("사용자 프로필 조회 요청, correlationId={}, userId={}", finalCorrelationId, userId)
        val query = GetUserProfileQuery(userId)
        val result = getUserProfileQueryHandler.handle(query, finalCorrelationId)
        val response = result.toResponse()
        response.add(
            linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, finalCorrelationId)).withSelfRel(),
        )
        log.info("사용자 프로필 조회 성공, correlationId={}, userId={}", finalCorrelationId, userId)
        return ResponseEntity.ok(response)
    }
}
