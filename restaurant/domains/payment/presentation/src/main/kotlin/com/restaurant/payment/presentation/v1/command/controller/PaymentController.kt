package com.restaurant.payment.presentation.v1.command.controller

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.payment.application.command.usecase.ProcessPaymentUseCase
import com.restaurant.payment.application.command.usecase.RefundPaymentUseCase
import com.restaurant.payment.presentation.v1.command.dto.request.ProcessPaymentRequestV1
import com.restaurant.payment.presentation.v1.command.dto.request.RefundPaymentRequestV1
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private val log = KotlinLogging.logger {}

@Tag(name = "Payment Commands", description = "결제 처리 API")
@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val refundPaymentUseCase: RefundPaymentUseCase,
) {
    @PostMapping("/orders/{orderId}/users/{userId}/pay")
    @Operation(
        summary = "주문 결제 요청",
        description = "새로운 주문에 대한 결제를 처리합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "결제 요청 성공",
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
                responseCode = "402",
                description = "결제 실패 (Payment Required)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    suspend fun processPayment(
        @Parameter(description = "주문 ID", required = true, example = "order-123")
        @PathVariable orderId: String,
        @Parameter(description = "사용자 ID", required = true, example = "user-456")
        @PathVariable userId: String,
        @Parameter(description = "결제 요청 정보", required = true, schema = Schema(implementation = ProcessPaymentRequestV1::class))
        @Valid
        @RequestBody request: ProcessPaymentRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Processing payment request for order: $orderId, user: $userId" }

        val command = request.toCommand(orderId, userId)
        val paymentId: String = processPaymentUseCase.execute(command)

        val location =
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/payments/{paymentId}")
                .buildAndExpand(paymentId)
                .toUri()

        log.info { "Payment processing initiated successfully, paymentId: $paymentId" }

        return ResponseEntity.created(location).body(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Payment processing initiated successfully.",
            ),
        )
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(
        summary = "결제 취소 (환불) 요청",
        description = "기존 결제에 대한 환불을 처리합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "환불 요청 성공",
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
                responseCode = "404",
                description = "결제를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "422",
                description = "환불 처리 불가 (Unprocessable Entity)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    suspend fun refundPayment(
        @Parameter(description = "환불할 결제의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable paymentId: String,
        @Parameter(description = "환불 요청 정보", required = true, schema = Schema(implementation = RefundPaymentRequestV1::class))
        @Valid
        @RequestBody request: RefundPaymentRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Processing refund request for payment: $paymentId" }

        val command = request.toCommand(paymentId)
        val refundId: String = refundPaymentUseCase.execute(command)

        log.info { "Refund processing initiated successfully, refundId: $refundId" }

        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Refund processing initiated successfully.",
            ),
        )
    }
}
