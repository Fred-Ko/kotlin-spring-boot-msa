package com.restaurant.presentation.user.v1.command

import com.restaurant.application.user.command.handler.ChangePasswordCommandHandler
import com.restaurant.application.user.command.handler.DeleteUserCommandHandler
import com.restaurant.application.user.command.handler.LoginCommandHandler
import com.restaurant.application.user.command.handler.RegisterUserCommandHandler
import com.restaurant.application.user.command.handler.UpdateProfileCommandHandler
import com.restaurant.presentation.user.v1.dto.request.ChangePasswordRequestV1
import com.restaurant.presentation.user.v1.dto.request.DeleteUserRequestV1
import com.restaurant.presentation.user.v1.dto.request.LoginRequestV1
import com.restaurant.presentation.user.v1.dto.request.RegisterUserRequestV1
import com.restaurant.presentation.user.v1.dto.request.UpdateProfileRequestV1
import com.restaurant.presentation.user.v1.extensions.request.toCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
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
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 관리", description = "사용자 등록, 로그인, 프로필 관리 등의 API")
class UserCommandControllerV1(
    private val registerUserCommandHandler: RegisterUserCommandHandler,
    private val loginCommandHandler: LoginCommandHandler,
    private val updateProfileCommandHandler: UpdateProfileCommandHandler,
    private val changePasswordCommandHandler: ChangePasswordCommandHandler,
    private val deleteUserCommandHandler: DeleteUserCommandHandler,
) {
    private val log = LoggerFactory.getLogger(UserCommandControllerV1::class.java)

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
        @Valid @RequestBody request: RegisterUserRequestV1,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val command = request.toCommand()

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        registerUserCommandHandler.handle(command, correlationId)

        // 성공 시 응답
        val profileUri = "/api/v1/users/profile"
        return ResponseEntity
            .created(URI.create(profileUri))
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "회원 가입이 완료되었습니다.",
                    "correlationId" to correlationId,
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
        @Valid @RequestBody request: LoginRequestV1,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val command = request.toCommand()

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        loginCommandHandler.handle(command, correlationId)

        // 성공 시 응답 - 실제로는 여기서 JWT 토큰 등을 생성해야 함
        return ResponseEntity
            .ok()
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "로그인이 완료되었습니다.",
                    "correlationId" to correlationId,
                    "token" to "dummy-token-$correlationId",
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
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "프로필 수정", description = "사용자 프로필 정보를 수정합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "프로필 수정 성공",
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
    fun updateProfile(
        @Parameter(description = "사용자 ID", required = true)
        @PathVariable userId: Long,
        @Valid @RequestBody request: UpdateProfileRequestV1,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val command = request.toCommand(userId)

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        updateProfileCommandHandler.handle(command, correlationId)

        // 성공 시 응답
        return ResponseEntity
            .ok()
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "프로필이 수정되었습니다.",
                    "correlationId" to correlationId,
                ),
            )
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
                    description = "잘못된 요청 데이터 또는 기존 비밀번호 불일치",
                    content = [Content(mediaType = "application/problem+json")],
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = [Content(mediaType = "application/problem+json")],
                ),
            ],
    )
    fun changePassword(
        @Parameter(description = "사용자 ID", required = true)
        @PathVariable userId: Long,
        @Valid @RequestBody request: ChangePasswordRequestV1,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val command = request.toCommand(userId)

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        changePasswordCommandHandler.handle(command, correlationId)

        // 성공 시 응답
        return ResponseEntity
            .ok()
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "비밀번호가 변경되었습니다.",
                    "correlationId" to correlationId,
                ),
            )
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = [Content(mediaType = "application/json")],
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터 또는 비밀번호 불일치",
                    content = [Content(mediaType = "application/problem+json")],
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = [Content(mediaType = "application/problem+json")],
                ),
            ],
    )
    fun deleteUser(
        @Parameter(description = "사용자 ID", required = true)
        @PathVariable userId: Long,
        @Valid @RequestBody request: DeleteUserRequestV1,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val command = request.toCommand(userId)

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        deleteUserCommandHandler.handle(command, correlationId)

        // 성공 시 응답
        return ResponseEntity
            .ok()
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "회원 탈퇴가 완료되었습니다.",
                    "correlationId" to correlationId,
                ),
            )
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
    fun logout(): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()

        // 실제 로그아웃 처리 로직 구현 필요

        return ResponseEntity.ok().body(
            mapOf(
                "status" to "SUCCESS",
                "message" to "로그아웃이 완료되었습니다.",
                "correlationId" to correlationId,
            ),
        )
    }
}
