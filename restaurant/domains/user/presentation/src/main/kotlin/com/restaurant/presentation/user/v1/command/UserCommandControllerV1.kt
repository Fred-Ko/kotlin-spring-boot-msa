package com.restaurant.presentation.user.v1.command
import com.restaurant.application.user.command.handler.UpdateProfileCommandHandler
import com.restaurant.application.user.handler.ChangePasswordCommandHandler
import com.restaurant.application.user.handler.DeleteUserCommandHandler
import com.restaurant.application.user.handler.LoginCommandHandler
import com.restaurant.application.user.handler.RegisterUserCommandHandler
import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.presentation.user.extensions.v1.request.toCommand
import com.restaurant.presentation.user.v1.command.dto.request.ChangePasswordRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.DeleteUserRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.LoginRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.RegisterUserRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UpdateProfileRequestV1
import com.restaurant.presentation.user.v1.command.dto.response.LoginResponseV1
import com.restaurant.presentation.user.v1.query.UserQueryControllerV1
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "사용자 관리", description = "사용자 등록, 로그인, 프로필 관리 API")
class UserCommandControllerV1(
    private val registerUserCommandHandler: RegisterUserCommandHandler,
    private val loginCommandHandler: LoginCommandHandler,
    private val updateProfileCommandHandler: UpdateProfileCommandHandler,
    private val changePasswordCommandHandler: ChangePasswordCommandHandler,
    private val deleteUserCommandHandler: DeleteUserCommandHandler,
) {
    private val log = LoggerFactory.getLogger(UserCommandControllerV1::class.java)

    private fun getOrGenerateCorrelationId(headerValue: String?): String =
        if (!headerValue.isNullOrBlank()) headerValue else UUID.randomUUID().toString()

    @PostMapping("/register")
    @Operation(summary = "사용자 등록", description = "신규 사용자를 시스템에 등록합니다. 이메일, 이름, 비밀번호가 필요합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "사용자 등록 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터",
                content = [Content(mediaType = "application/problem+json")],
            ),
            ApiResponse(
                responseCode = "409",
                description = "이미 존재하는 이메일",
                content = [Content(mediaType = "application/problem+json")],
            ),
        ],
    )
    fun registerUser(
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Valid @RequestBody request: RegisterUserRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        log.info("사용자 등록 요청: email={}, correlationId={}", request.email, correlationId)

        // extension 함수 사용
        val userId = registerUserCommandHandler.handle(request.toCommand(), correlationId)

        log.info("사용자 등록 성공: userId={}, correlationId={}", userId, correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "사용자가 성공적으로 등록되었습니다.",
                correlationId = correlationId,
            )
        response.add(linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).withRel("user-profile"))

        return ResponseEntity
            .created(
                linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).toUri(),
            ).body(response)
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = LoginResponseV1::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터",
                content = [Content(mediaType = "application/problem+json")],
            ),
            ApiResponse(
                responseCode = "401",
                description = "로그인 실패 (이메일 또는 비밀번호 불일치)",
                content = [Content(mediaType = "application/problem+json")],
            ),
        ],
    )
    fun login(
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Valid @RequestBody request: LoginRequestV1,
    ): ResponseEntity<LoginResponseV1> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        log.info("로그인 요청: email={}, correlationId={}", request.email, correlationId)

        val loginResult = loginCommandHandler.handle(request.toCommand(), correlationId)

        log.info("로그인 성공: userId={}, correlationId={}", loginResult, correlationId)

        val response =
            LoginResponseV1(
                status = "SUCCESS",
                message = "로그인 성공",
                userId = loginResult,
                accessToken = "",
                refreshToken = "",
                correlationId = correlationId,
            )

        response.add(
            linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(loginResult, correlationId)).withRel("user-profile"),
        )

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "프로필 수정", description = "사용자 프로필 정보를 수정합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "프로필 수정 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
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
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Parameter(description = "사용자 ID", required = true) @PathVariable userId: String,
        @Valid @RequestBody request: UpdateProfileRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        log.info("프로필 수정 요청: userId={}, correlationId={}", userId, correlationId)

        updateProfileCommandHandler.handle(request.toCommand(userId), correlationId)

        log.info("프로필 수정 성공: userId={}, correlationId={}", userId, correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "프로필이 성공적으로 수정되었습니다.",
                correlationId = correlationId,
            )
        response.add(linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).withRel("user-profile"))

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "비밀번호 변경", description = "사용자 비밀번호를 변경합니다. 현재 비밀번호 확인이 필요합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "비밀번호 변경 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터",
                content = [Content(mediaType = "application/problem+json")],
            ),
            ApiResponse(
                responseCode = "401",
                description = "현재 비밀번호가 일치하지 않음",
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
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Parameter(description = "사용자 ID", required = true) @PathVariable userId: String,
        @Valid @RequestBody request: ChangePasswordRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        log.info("비밀번호 변경 요청: userId={}, correlationId={}", userId, correlationId)

        changePasswordCommandHandler.handle(request.toCommand(userId), correlationId)

        log.info("비밀번호 변경 성공: userId={}, correlationId={}", userId, correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "비밀번호가 성공적으로 변경되었습니다.",
                correlationId = correlationId,
            )
        response.add(linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).withRel("user-profile"))

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "사용자 탈퇴", description = "사용자 계정을 삭제합니다. 비밀번호 확인이 필요합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용자 탈퇴 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터",
                content = [Content(mediaType = "application/problem+json")],
            ),
            ApiResponse(
                responseCode = "401",
                description = "비밀번호가 일치하지 않음",
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
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Parameter(description = "사용자 ID", required = true) @PathVariable userId: String,
        @Valid @RequestBody request: DeleteUserRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        log.info("사용자 탈퇴 요청: userId={}, correlationId={}", userId, correlationId)

        deleteUserCommandHandler.handle(request.toCommand(userId), correlationId)

        log.info("사용자 탈퇴 성공: userId={}, correlationId={}", userId, correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "사용자 계정이 성공적으로 삭제되었습니다.",
                correlationId = correlationId,
            )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자 세션을 종료합니다. (토큰 기반 인증에서는 클라이언트 측 토큰 삭제로 처리)")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그아웃 요청 처리됨",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
        ],
    )
    fun logout(
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        log.info("로그아웃 요청: correlationId={}", correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "로그아웃 요청이 처리되었습니다.",
                correlationId = correlationId,
            )
        response.add(
            linkTo(methodOn(UserCommandControllerV1::class.java).login(correlationId, LoginRequestV1("", ""))).withRel("login"),
        )

        return ResponseEntity.ok(response)
    }
}
