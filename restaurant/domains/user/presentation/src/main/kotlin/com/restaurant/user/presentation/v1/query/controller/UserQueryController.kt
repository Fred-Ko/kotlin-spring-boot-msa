package com.restaurant.user.presentation.v1.query.controller

import com.restaurant.user.presentation.v1.query.dto.response.UserProfileResponseV1
import com.restaurant.user.presentation.v1.query.extensions.dto.response.toResponseV1

import com.restaurant.user.application.dto.query.GetUserProfileByIdQuery
import com.restaurant.user.application.port.GetUserProfileQuery
import com.restaurant.user.domain.vo.UserId
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Queries", description = "API for querying user information")
class UserQueryController(
    private val getUserProfileQuery: GetUserProfileQuery
) {
    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile", description = "Retrieves user profile information by User ID.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Profile retrieved successfully",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UserProfileResponseV1::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid UUID format",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun getUserProfile(
        @PathVariable userId: UUID,
        @RequestHeader("X-Correlation-Id") correlationId: String
    ): ResponseEntity<UserProfileResponseV1> {
        log.info { "[Correlation ID: $correlationId] Received request to get profile for user ID: $userId" }
        val query = GetUserProfileByIdQuery(userId = UserId.of(userId).value.toString())
        val userProfileDto = getUserProfileQuery.getUserProfile(query)
        val responseDto = userProfileDto.toResponseV1(correlationId)

        responseDto.add(
            linkTo(methodOn(UserQueryController::class.java).getUserProfile(userId, correlationId)).withSelfRel()
            // TODO: Add update-profile and change-password links if needed, ensure UserController is imported and available
        )
        log.info { "Returning user profile for ID: $userId" }
        return ResponseEntity.ok(responseDto)
    }
}
