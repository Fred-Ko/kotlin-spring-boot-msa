package com.restaurant.user.presentation.v1.controller

import com.restaurant.user.application.dto.query.GetUserProfileByIdQuery
import com.restaurant.user.application.port.`in`.GetUserProfileQuery
import com.restaurant.user.presentation.v1.dto.response.UserProfileResponseV1
import com.restaurant.user.presentation.v1.extensions.response.toResponseV1
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Queries", description = "API for querying user information")
class UserQueryController(
    private val getUserProfileQueryUseCase: GetUserProfileQuery,
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
        @Parameter(description = "User ID (UUID)", required = true) @PathVariable userId: String,
    ): ResponseEntity<UserProfileResponseV1> {
        log.info { "Received request to get user profile for ID: $userId" }

        val query = GetUserProfileByIdQuery(userId = userId)
        val userProfileDto = getUserProfileQueryUseCase.getUserProfile(query)

        val responseDto = userProfileDto.toResponseV1()

        val userUuid = UUID.fromString(userId)
        responseDto.add(
            linkTo(methodOn(UserQueryController::class.java).getUserProfile(userId)).withSelfRel(),
            linkTo(methodOn(UserController::class.java).updateProfile(userUuid, null)).withRel("update-profile"),
            linkTo(methodOn(UserController::class.java).changePassword(userUuid, null)).withRel("change-password"),
        )
        log.info { "Returning user profile for ID: $userId" }
        return ResponseEntity.ok(responseDto)
    }
}
