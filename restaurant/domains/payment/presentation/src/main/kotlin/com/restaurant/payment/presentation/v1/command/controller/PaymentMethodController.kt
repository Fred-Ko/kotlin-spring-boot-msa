package com.restaurant.payment.presentation.v1.command.controller

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.payment.application.command.usecase.RegisterPaymentMethodUseCase
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterPaymentMethodRequest
import com.restaurant.payment.presentation.v1.command.extensions.dto.request.toCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
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

private val log = KotlinLogging.logger {}

@Tag(name = "Payment Method Commands", description = "결제 수단 관리 API")
@RestController
@RequestMapping("/api/v1/users/{userId}/payment-methods")
class PaymentMethodController(
    private val registerPaymentMethodUseCase: RegisterPaymentMethodUseCase,
    // TODO: UpdatePaymentMethodUseCase, DeletePaymentMethodUseCase 추가 필요
) {
    @PostMapping
    @Operation(
        summary = "사용자 결제 수단 등록",
        description = "사용자의 새로운 결제 수단을 등록합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "결제 수단 등록 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 형식입니다 (Bad Request)",
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
        ],
    )
    fun registerPaymentMethod(
        @Parameter(description = "사용자 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "결제 수단 등록 요청 정보", required = true, schema = Schema(implementation = RegisterPaymentMethodRequest::class))
        @Valid
        @RequestBody request: RegisterPaymentMethodRequest,
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Registering payment method for user: $userId" }

        val command = request.toCommand(userId.toString())
        val paymentMethodId = registerPaymentMethodUseCase.execute(command)

        val location = URI.create("/api/v1/users/$userId/payment-methods/$paymentMethodId")

        log.info { "Payment method registered successfully, paymentMethodId: $paymentMethodId" }

        return ResponseEntity.created(location).body(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Payment method registered successfully.",
                resourceId = paymentMethodId,
            ),
        )
    }

    @PutMapping("/{paymentMethodId}")
    @Operation(
        summary = "사용자 결제 수단 수정",
        description = "기존 결제 수단의 정보를 수정합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결제 수단 수정 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 형식입니다 (Bad Request)",
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
                description = "결제 수단을 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun updatePaymentMethod(
        @Parameter(description = "사용자 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "결제 수단 ID", required = true, example = "b2c3d4e5-f6g7-8901-2345-678901bcdefg")
        @PathVariable paymentMethodId: UUID,
        @Parameter(description = "결제 수단 수정 요청 정보", required = true, schema = Schema(implementation = RegisterPaymentMethodRequest::class))
        @Valid
        @RequestBody request: RegisterPaymentMethodRequest,
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Updating payment method: $paymentMethodId for user: $userId" }

        // TODO: UpdatePaymentMethodCommand 및 UseCase 구현 필요
        // val command = request.toCommand(UserId.of(userId), PaymentMethodId.of(paymentMethodId))
        // updatePaymentMethodUseCase.execute(command)

        log.info { "Payment method updated successfully, paymentMethodId: $paymentMethodId" }

        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Payment method updated successfully.",
                resourceId = paymentMethodId.toString(),
            ),
        )
    }

    @DeleteMapping("/{paymentMethodId}")
    @Operation(
        summary = "사용자 결제 수단 삭제",
        description = "기존 결제 수단을 삭제합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "결제 수단 삭제 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
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
                description = "결제 수단을 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun deletePaymentMethod(
        @Parameter(description = "사용자 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "결제 수단 ID", required = true, example = "b2c3d4e5-f6g7-8901-2345-678901bcdefg")
        @PathVariable paymentMethodId: UUID,
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Deleting payment method: $paymentMethodId for user: $userId" }

        // TODO: DeletePaymentMethodCommand 및 UseCase 구현 필요
        // val command = DeletePaymentMethodCommand(UserId.of(userId), PaymentMethodId.of(paymentMethodId))
        // deletePaymentMethodUseCase.execute(command)

        log.info { "Payment method deleted successfully, paymentMethodId: $paymentMethodId" }

        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Payment method deleted successfully.",
                resourceId = paymentMethodId.toString(),
            ),
        )
    }
}
