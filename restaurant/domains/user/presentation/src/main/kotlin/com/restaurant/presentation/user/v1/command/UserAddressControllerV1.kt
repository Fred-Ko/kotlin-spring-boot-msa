package com.restaurant.presentation.user.v1.command

import com.restaurant.application.user.command.DeleteAddressCommand
import com.restaurant.application.user.command.handler.DeleteAddressCommandHandler
import com.restaurant.application.user.command.handler.RegisterAddressCommandHandler
import com.restaurant.application.user.command.handler.UpdateAddressCommandHandler
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.presentation.user.v1.command.dto.request.UserAddressRegisterRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserAddressUpdateRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.toCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Instant

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
@Tag(name = "주소 관리", description = "사용자의 배달 주소 등록, 수정, 삭제 API")
class UserAddressControllerV1(
  private val registerAddressCommandHandler: RegisterAddressCommandHandler,
  private val updateAddressCommandHandler: UpdateAddressCommandHandler,
  private val deleteAddressCommandHandler: DeleteAddressCommandHandler,
  @Value("\${app.problem.base-url}") private val problemBaseUrl: String,
) {
  @PostMapping
  @Operation(summary = "주소 등록", description = "사용자의 새로운 배달 주소를 등록합니다.")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "201",
        description = "주소 등록 성공",
        content = [Content(mediaType = "application/json")],
      ),
      ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터",
        content = [Content(mediaType = "application/problem+json")],
      ),
      ApiResponse(
        responseCode = "404",
        description = "사용자를 찾을 수 없음",
        content = [Content(mediaType = "application/problem+json")],
      ),
    ],
  )
  fun registerAddress(
    @Parameter(description = "사용자 ID", required = true)
    @PathVariable userId: Long,
    @Valid @RequestBody request: UserAddressRegisterRequestV1,
  ): ResponseEntity<Any> {
    val command = request.toCommand(userId)
    val result = registerAddressCommandHandler.handle(command)

    return if (result.success) {
      val addressUri = "/api/v1/users/$userId/addresses/${result.correlationId}"
      ResponseEntity
        .created(URI.create(addressUri))
        .body(
          mapOf(
            "status" to "success",
            "message" to "주소가 등록되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
      val problem =
        ProblemDetail.forStatus(error.status).apply {
          type = URI.create("$problemBaseUrl/${error.code.lowercase()}")
          title =
            error.code
              .replace("_", " ")
              .lowercase()
              .replaceFirstChar { it.uppercase() }
          detail = error.message
          instance = URI.create("/api/v1/users/$userId/addresses")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      ResponseEntity.status(error.status).body(problem)
    }
  }

  @PutMapping("/{addressId}")
  @Operation(summary = "주소 수정", description = "등록된 배달 주소를 수정합니다.")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "주소 수정 성공",
        content = [Content(mediaType = "application/json")],
      ),
      ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터",
        content = [Content(mediaType = "application/problem+json")],
      ),
      ApiResponse(
        responseCode = "404",
        description = "사용자 또는 주소를 찾을 수 없음",
        content = [Content(mediaType = "application/problem+json")],
      ),
    ],
  )
  fun updateAddress(
    @Parameter(description = "사용자 ID", required = true)
    @PathVariable userId: Long,
    @Parameter(description = "주소 ID", required = true)
    @PathVariable addressId: Long,
    @Valid @RequestBody request: UserAddressUpdateRequestV1,
  ): ResponseEntity<Any> {
    val command = request.toCommand(userId, addressId)
    val result = updateAddressCommandHandler.handle(command)

    return if (result.success) {
      ResponseEntity
        .ok()
        .body(
          mapOf(
            "status" to "success",
            "message" to "주소가 수정되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
      val problem =
        ProblemDetail.forStatus(error.status).apply {
          type = URI.create("$problemBaseUrl/${error.code.lowercase()}")
          title =
            error.code
              .replace("_", " ")
              .lowercase()
              .replaceFirstChar { it.uppercase() }
          detail = error.message
          instance = URI.create("/api/v1/users/$userId/addresses/$addressId")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      ResponseEntity.status(error.status).body(problem)
    }
  }

  @DeleteMapping("/{addressId}")
  @Operation(summary = "주소 삭제", description = "등록된 배달 주소를 삭제합니다.")
  @ApiResponses(
    value = [
      ApiResponse(
        responseCode = "200",
        description = "주소 삭제 성공",
        content = [Content(mediaType = "application/json")],
      ),
      ApiResponse(
        responseCode = "404",
        description = "사용자 또는 주소를 찾을 수 없음",
        content = [Content(mediaType = "application/problem+json")],
      ),
    ],
  )
  fun deleteAddress(
    @Parameter(description = "사용자 ID", required = true)
    @PathVariable userId: Long,
    @Parameter(description = "주소 ID", required = true)
    @PathVariable addressId: Long,
  ): ResponseEntity<Any> {
    val command = DeleteAddressCommand(userId, addressId)
    val result = deleteAddressCommandHandler.handle(command)

    return if (result.success) {
      ResponseEntity
        .ok()
        .body(
          mapOf(
            "status" to "success",
            "message" to "주소가 삭제되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
      val problem =
        ProblemDetail.forStatus(error.status).apply {
          type = URI.create("$problemBaseUrl/${error.code.lowercase()}")
          title =
            error.code
              .replace("_", " ")
              .lowercase()
              .replaceFirstChar { it.uppercase() }
          detail = error.message
          instance = URI.create("/api/v1/users/$userId/addresses/$addressId")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      ResponseEntity.status(error.status).body(problem)
    }
  }
}
