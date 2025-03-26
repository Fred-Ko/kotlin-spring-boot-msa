package com.restaurant.presentation.user.v1.command

import com.restaurant.application.user.command.handler.ChangePasswordCommandHandler
import com.restaurant.application.user.command.handler.DeleteUserCommandHandler
import com.restaurant.application.user.command.handler.LoginCommandHandler
import com.restaurant.application.user.command.handler.RegisterUserCommandHandler
import com.restaurant.application.user.command.handler.UpdateProfileCommandHandler
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.presentation.user.v1.command.dto.request.UserChangePasswordRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserDeleteRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserLoginRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserRegisterRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserUpdateProfileRequestV1
import com.restaurant.presentation.user.v1.mapper.UserMapperV1
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
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
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 관리", description = "사용자 등록, 로그인, 프로필 관리 등의 API")
class UserCommandControllerV1(
  private val registerUserCommandHandler: RegisterUserCommandHandler,
  private val loginCommandHandler: LoginCommandHandler,
  private val updateProfileCommandHandler: UpdateProfileCommandHandler,
  private val changePasswordCommandHandler: ChangePasswordCommandHandler,
  private val deleteUserCommandHandler: DeleteUserCommandHandler,
  private val userMapper: UserMapperV1,
  @Value("\${app.problem.base-url}") private val problemBaseUrl: String,
) {
  @PostMapping("/register")
  @Operation(summary = "사용자 등록", description = "새로운 사용자를 시스템에 등록합니다.")
  @ApiResponses(
    value =
      [
        ApiResponse(
          responseCode = "201",
          description = "사용자 등록 성공",
          content = [Content(mediaType = "application/json")],
        ),
        ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 데이터",
          content = [Content(mediaType = "application/problem+json")],
        ),
        ApiResponse(
          responseCode = "409",
          description = "이미 존재하는 사용자",
          content = [Content(mediaType = "application/problem+json")],
        ),
      ],
  )
  fun register(
    @Valid @RequestBody request: UserRegisterRequestV1,
  ): ResponseEntity<Any> {
    val command = userMapper.toRegisterUserCommand(request)
    val result = registerUserCommandHandler.handle(command)

    return if (result.success) {
      val profileUri = "/api/v1/users/${result.correlationId}"
      ResponseEntity
        .created(URI.create(profileUri))
        .body(
          mapOf(
            "status" to "success",
            "message" to "회원 가입이 완료되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
            "links" to
              listOf(
                mapOf(
                  "rel" to "user-profile",
                  "href" to profileUri,
                  "method" to "GET",
                ),
              ),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
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
          instance = URI.create("/api/v1/users/register")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      ResponseEntity.status(error.status).body(problem)
    }
  }

  @PostMapping("/login")
  @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호로 사용자 인증을 수행합니다.")
  @ApiResponses(
    value =
      [
        ApiResponse(
          responseCode = "200",
          description = "로그인 성공",
          content = [Content(mediaType = "application/json")],
        ),
        ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 데이터",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
        ApiResponse(
          responseCode = "401",
          description = "인증 실패",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
      ],
  )
  fun login(
    @Valid @RequestBody request: UserLoginRequestV1,
  ): ResponseEntity<Any> {
    val command = userMapper.toLoginCommand(request)
    val result = loginCommandHandler.handle(command)

    return if (result.success) {
      ResponseEntity
        .ok()
        .body(
          mapOf(
            "status" to "success",
            "message" to "로그인이 완료되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
            "token" to "dummy-token-${result.correlationId}",
            "links" to
              listOf(
                mapOf(
                  "rel" to "logout",
                  "href" to "/api/v1/users/logout",
                  "method" to "POST",
                ),
              ),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
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
          instance = URI.create("/api/v1/users/login")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      ResponseEntity.status(error.status).body(problem)
    }
  }

  @PutMapping("/{userId}/profile")
  @Operation(summary = "사용자 프로필 업데이트", description = "사용자 프로필 정보를 업데이트합니다.")
  @ApiResponses(
    value =
      [
        ApiResponse(
          responseCode = "200",
          description = "프로필 업데이트 성공",
          content = [Content(mediaType = "application/json")],
        ),
        ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 데이터",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
        ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
      ],
  )
  fun updateProfile(
    @Parameter(description = "사용자 ID", example = "1") @PathVariable userId: Long,
    @Valid @RequestBody request: UserUpdateProfileRequestV1,
  ): ResponseEntity<Any> {
    val command = userMapper.toUpdateProfileCommand(userId, request)
    val result = updateProfileCommandHandler.handle(command)

    return if (result.success) {
      val profileUri = "/api/v1/users/$userId"
      ResponseEntity
        .ok()
        .body(
          mapOf(
            "status" to "success",
            "message" to "프로필이 업데이트 되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
            "links" to
              listOf(
                mapOf(
                  "rel" to "user-profile",
                  "href" to profileUri,
                  "method" to "GET",
                ),
              ),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
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
          instance = URI.create("/api/v1/users/$userId/profile")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      ResponseEntity.status(error.status).body(problem)
    }
  }

  @PutMapping("/{userId}/password")
  @Operation(summary = "비밀번호 변경", description = "사용자 비밀번호를 변경합니다.")
  @ApiResponses(
    value =
      [
        ApiResponse(
          responseCode = "200",
          description = "비밀번호 변경 성공",
          content = [Content(mediaType = "application/json")],
        ),
        ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 데이터",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
        ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
      ],
  )
  fun changePassword(
    @Parameter(description = "사용자 ID", example = "1") @PathVariable userId: Long,
    @Valid @RequestBody request: UserChangePasswordRequestV1,
  ): ResponseEntity<Any> {
    val command = userMapper.toChangePasswordCommand(userId, request)
    val result = changePasswordCommandHandler.handle(command)

    return if (result.success) {
      ResponseEntity
        .ok()
        .body(
          mapOf(
            "status" to "success",
            "message" to "비밀번호가 변경되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
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
          instance = URI.create("/api/v1/users/$userId/password")
          setProperty("errorCode", error.code)
          setProperty("timestamp", Instant.now().toString())
        }
      ResponseEntity.status(error.status).body(problem)
    }
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "사용자 삭제", description = "사용자 계정을 삭제합니다.")
  @ApiResponses(
    value =
      [
        ApiResponse(
          responseCode = "200",
          description = "사용자 삭제 성공",
          content = [Content(mediaType = "application/json")],
        ),
        ApiResponse(
          responseCode = "400",
          description = "잘못된 요청 데이터",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
        ApiResponse(
          responseCode = "404",
          description = "사용자를 찾을 수 없음",
          content =
            [
              Content(
                mediaType = "application/problem+json",
                schema =
                  Schema(
                    implementation =
                      ProblemDetail::class,
                  ),
              ),
            ],
        ),
      ],
  )
  fun deleteUser(
    @Parameter(description = "사용자 ID", example = "1") @PathVariable userId: Long,
    @Valid @RequestBody request: UserDeleteRequestV1,
  ): ResponseEntity<Any> {
    val command = userMapper.toDeleteUserCommand(userId, request)
    val result = deleteUserCommandHandler.handle(command)

    return if (result.success) {
      ResponseEntity
        .ok()
        .body(
          mapOf(
            "status" to "success",
            "message" to "회원 탈퇴가 완료되었습니다.",
            "correlationId" to (result.correlationId ?: ""),
          ),
        )
    } else {
      val error = UserErrorCode.fromCode(result.errorCode)
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
      ResponseEntity.status(error.status).body(problem)
    }
  }

  @PostMapping("/logout")
  @Operation(summary = "로그아웃", description = "현재 사용자 세션을 종료합니다.")
  @ApiResponses(
    value =
      [
        ApiResponse(
          responseCode = "200",
          description = "로그아웃 성공",
          content = [Content(mediaType = "application/json")],
        ),
      ],
  )
  fun logout(): ResponseEntity<Map<String, Any>> =
    ResponseEntity
      .ok()
      .body(
        mapOf(
          "status" to "success",
          "message" to "로그아웃 되었습니다.",
          "links" to
            listOf(
              mapOf(
                "rel" to "login",
                "href" to "/api/v1/users/login",
                "method" to "POST",
              ),
            ),
        ),
      )
}
