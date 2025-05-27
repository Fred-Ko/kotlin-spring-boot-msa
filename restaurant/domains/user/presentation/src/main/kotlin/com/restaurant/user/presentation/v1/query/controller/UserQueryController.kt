package com.restaurant.user.presentation.v1.query.controller

import com.restaurant.user.application.query.usecase.GetUserProfileQuery
import com.restaurant.user.application.query.dto.GetUserProfileByIdQuery
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.query.dto.response.UserProfileResponseV1
import com.restaurant.user.presentation.v1.query.extensions.dto.response.toResponseV1
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import io.swagger.v3.oas.annotations.Parameter

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Queries", description = "사용자 정보 조회 API")
class UserQueryController(
    private val getUserProfileQuery: GetUserProfileQuery
) {
    @GetMapping("/{userId}/profile")
    @Operation(
        summary = "사용자 프로필 조회", 
        description = "사용자 ID로 프로필 정보를 조회합니다.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "프로필 조회 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UserProfileResponseV1::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 UUID 형식",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "접근 권한 없음 (Forbidden)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun getUserProfile(
        @Parameter(description = "조회할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        
    ): ResponseEntity<UserProfileResponseV1> {
        log.info { "Received request to get profile for user ID: $userId" }
        val query = GetUserProfileByIdQuery(userId = UserId.of(userId).value.toString())
        val userProfileDto = getUserProfileQuery.getUserProfile(query)
        val responseDto = userProfileDto.toResponseV1()

        log.info { "Returning user profile for ID: $userId" }
        return ResponseEntity.ok(responseDto)
    }
}
