package com.restaurant.user.presentation.v1.command.controller

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.user.presentation.v1.query.dto.response.LoginResponseV1
import com.restaurant.user.application.command.usecase.RegisterUserUseCase
import com.restaurant.user.application.command.usecase.LoginUseCase
import com.restaurant.user.application.command.usecase.UpdateProfileUseCase
import com.restaurant.user.application.command.usecase.ChangePasswordUseCase
import com.restaurant.user.application.command.usecase.DeleteUserUseCase
import com.restaurant.user.application.command.dto.RegisterUserCommand
import com.restaurant.user.application.command.dto.LoginCommand
import com.restaurant.user.application.command.dto.UpdateProfileCommand
import com.restaurant.user.application.command.dto.ChangePasswordCommand
import com.restaurant.user.application.command.dto.DeleteUserCommand
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.command.dto.request.RegisterUserRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.LoginRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateProfileRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.ChangePasswordRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.DeleteUserRequestV1
import com.restaurant.user.presentation.v1.command.extensions.dto.request.toCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID
import org.springframework.http.ProblemDetail

private val log = KotlinLogging.logger {}

@Tag(name = "User Commands", description = "사용자 계정 관리 API")
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) {
    @PostMapping("/register")
    @Operation(
        summary = "사용자 회원가입", 
        description = "새로운 사용자 계정을 생성합니다.",
        security = [] // 회원가입은 인증이 필요 없음
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201", description = "회원가입 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400", description = "잘못된 요청 형식입니다 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun registerUser(
        @Parameter(description = "회원가입 요청 정보", required = true, schema = Schema(implementation = RegisterUserRequestV1::class))
        @Valid @RequestBody request: RegisterUserRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand()
        val userId: UserId = registerUserUseCase.register(command)

        val location = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/v1/users/{userId}/profile")
            .buildAndExpand(userId.value)
            .toUri()

        return ResponseEntity.created(location).body(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User registered successfully."
            )
        )
    }

    @PostMapping("/login")
    @Operation(
        summary = "사용자 로그인", 
        description = "이메일과 비밀번호로 사용자 인증을 수행합니다.",
        security = [] // 로그인은 인증이 필요 없음
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "로그인 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = LoginResponseV1::class))]
            ),
            ApiResponse(
                responseCode = "400", description = "잘못된 요청 형식입니다 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "401", description = "인증 실패 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun login(
        @Parameter(description = "로그인 요청 정보", required = true, schema = Schema(implementation = LoginRequestV1::class))
        @Valid @RequestBody request: LoginRequestV1
    ): ResponseEntity<LoginResponseV1> {
        val command = request.toCommand()
        val loginResult = loginUseCase.login(command)
        return ResponseEntity.ok(
            LoginResponseV1(
                id = loginResult.id,
                username = loginResult.username,
                accessToken = loginResult.accessToken,
                refreshToken = loginResult.refreshToken
            )
        )
    }

    @PutMapping("/{userId}/profile")
    @Operation(
        summary = "사용자 프로필 수정", 
        description = "기존 사용자의 프로필 정보를 수정합니다.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "프로필 수정 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400", description = "잘못된 요청 형식입니다 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "401", description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "403", description = "접근 권한 없음 (Forbidden)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "404", description = "사용자를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun updateProfile(
        @Parameter(description = "수정할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "프로필 수정 요청 정보", required = true, schema = Schema(implementation = UpdateProfileRequestV1::class))
        @Valid @RequestBody request: UpdateProfileRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId))
        updateProfileUseCase.updateProfile(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User profile updated successfully."
            )
        )
    }

    @PatchMapping("/{userId}/password")
    @Operation(
        summary = "사용자 비밀번호 변경", 
        description = "기존 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "비밀번호 변경 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400", description = "잘못된 요청 형식 또는 현재 비밀번호 불일치 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "401", description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "403", description = "접근 권한 없음 (Forbidden)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "404", description = "사용자를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun changePassword(
        @Parameter(description = "비밀번호를 변경할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "비밀번호 변경 요청 정보", required = true, schema = Schema(implementation = ChangePasswordRequestV1::class))
        @Valid @RequestBody request: ChangePasswordRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId))
        changePasswordUseCase.changePassword(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User password changed successfully."
            )
        )
    }

    @DeleteMapping("/{userId}")
    @Operation(
        summary = "사용자 계정 삭제", 
        description = "사용자 계정을 영구적으로 삭제합니다.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "계정 삭제 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400", description = "잘못된 요청 형식입니다 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "401", description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "403", description = "접근 권한 없음 (Forbidden)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "404", description = "사용자를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun deleteUser(
        @Parameter(description = "삭제할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "계정 삭제 요청 정보 (필요시 현재 비밀번호 등)", required = true, schema = Schema(implementation = DeleteUserRequestV1::class))
        @Valid @RequestBody request: DeleteUserRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId))
        deleteUserUseCase.deleteUser(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "User deleted successfully."
            )
        )
    }
}
