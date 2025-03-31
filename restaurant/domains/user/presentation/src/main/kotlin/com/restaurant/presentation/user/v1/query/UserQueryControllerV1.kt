package com.restaurant.presentation.user.v1.query

import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserNotFoundApplicationException
import com.restaurant.application.user.query.GetUserProfileQuery
import com.restaurant.application.user.query.handler.GetUserProfileQueryHandler
import com.restaurant.presentation.user.v1.query.dto.response.UserProfileResponseV1
import com.restaurant.presentation.user.v1.query.dto.response.toResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Instant

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 조회", description = "사용자 프로필 조회 API")
class UserQueryControllerV1(
  private val getUserProfileQueryHandler: GetUserProfileQueryHandler,
  @Value("\${app.problem.base-url}") private val problemBaseUrl: String,
) {
  @GetMapping("/{userId}")
  @Operation(summary = "사용자 프로필 조회", description = "사용자 ID를 통해 프로필 정보를 조회합니다.")
  @ApiResponses(
    value =
      [
        ApiResponse(
          responseCode = "200",
          description = "프로필 조회 성공",
          content = [Content(mediaType = "application/json")],
        ),
        ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content = [Content(mediaType = "application/problem+json")],
        ),
      ],
  )
  fun getUserProfile(
    @Parameter(description = "사용자 ID", required = true) @PathVariable userId: Long,
  ): ResponseEntity<Any> {
    val query = GetUserProfileQuery(userId)

    try {
      val result = getUserProfileQueryHandler.handle(query)
      val response = result.toResponse()
      val responseWithLinks = addHateoasLinks(response, userId)
      return ResponseEntity.ok(responseWithLinks)
    } catch (e: UserNotFoundApplicationException) {
      val error = UserErrorCode.NOT_FOUND
      val problem =
        ProblemDetail.forStatus(error.status).apply {
          type =
            URI.create(
              "$problemBaseUrl/${error.code.lowercase()}",
            )
          title =
            error.code
              .replace("_", " ")
              .lowercase()
              .replaceFirstChar { it.uppercase() }
          detail = error.message
          instance = URI.create("/api/v1/users/$userId")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      return ResponseEntity.status(error.status).body(problem)
    } catch (e: Exception) {
      val error = UserErrorCode.SYSTEM_ERROR
      val problem =
        ProblemDetail.forStatus(error.status).apply {
          type = URI.create("$problemBaseUrl/${error.code.lowercase()}")
          title =
            error.code
              .replace("_", " ")
              .lowercase()
              .replaceFirstChar { it.uppercase() }
          detail = error.message
          instance = URI.create("/api/v1/users/$userId")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      return ResponseEntity.status(error.status).body(problem)
    }
  }

  private fun addHateoasLinks(
    response: UserProfileResponseV1,
    userId: Long,
  ): Map<String, Any> =
    mapOf(
      "data" to response,
      "links" to
        listOf(
          mapOf(
            "rel" to "self",
            "href" to "/api/v1/users/$userId",
            "method" to "GET",
          ),
          mapOf(
            "rel" to "update-profile",
            "href" to "/api/v1/users/$userId/profile",
            "method" to "PUT",
          ),
          mapOf(
            "rel" to "change-password",
            "href" to "/api/v1/users/$userId/password",
            "method" to "PUT",
          ),
          mapOf(
            "rel" to "delete-user",
            "href" to "/api/v1/users/$userId",
            "method" to "DELETE",
          ),
        ),
    )
}
