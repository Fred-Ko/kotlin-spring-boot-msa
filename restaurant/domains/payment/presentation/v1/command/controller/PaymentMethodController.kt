package com.restaurant.payment.presentation.v1.command.controller

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.payment.application.command.usecase.RegisterPaymentMethodUseCase
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.presentation.v1.command.dto.request.RegisterPaymentMethodRequest
import com.restaurant.payment.presentation.v1.command.extensions.dto.request.toCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@Tag(name = "Payment Methods", description = "결제 수단 관리 API")
@RestController
@RequestMapping("/api/v1/users/{userId}/payment-methods")
class PaymentMethodController(
    private val registerPaymentMethodUseCase: RegisterPaymentMethodUseCase,
) {
    @Operation(
        summary = "결제 수단 등록",
        description = "사용자의 새로운 결제 수단(신용카드, 계좌이체 등)을 등록합니다.",
        responses = [
            ApiResponse(responseCode = "201", description = "결제 수단 등록 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        ],
    )
    @PostMapping
    suspend fun registerPaymentMethod(
        @Parameter(description = "사용자 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Valid @RequestBody request: RegisterPaymentMethodRequest,
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId(userId))
        val paymentMethodId = registerPaymentMethodUseCase.registerPaymentMethod(command)
        val location = URI.create("/api/v1/users/$userId/payment-methods/${paymentMethodId.value}")

        return ResponseEntity.created(location).body(
            CommandResultResponse(
                id = paymentMethodId.value.toString(),
                message = "Payment method registered successfully.",
            ),
        )
    }

    // TODO: Implement other payment method management endpoints (update, delete, get list, get single)
}
