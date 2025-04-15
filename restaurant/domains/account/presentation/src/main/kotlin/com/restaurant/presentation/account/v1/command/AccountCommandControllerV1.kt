package com.restaurant.presentation.account.v1.command

import com.restaurant.application.account.command.handler.CancelAccountPaymentCommandHandler
import com.restaurant.application.account.command.handler.ProcessAccountPaymentCommandHandler
import com.restaurant.presentation.account.v1.dto.request.CancelPaymentRequestV1
import com.restaurant.presentation.account.v1.dto.request.ProcessPaymentRequestV1
import com.restaurant.presentation.account.v1.extensions.request.toCommand
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 계좌 커맨드 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/accounts")
class AccountCommandControllerV1(
    private val processAccountPaymentCommandHandler: ProcessAccountPaymentCommandHandler,
    private val cancelAccountPaymentCommandHandler: CancelAccountPaymentCommandHandler,
) {
    /**
     * 계좌 결제 처리
     */
    @PostMapping("/{accountId}/payments")
    fun processPayment(
        @PathVariable accountId: Long,
        @Valid @RequestBody request: ProcessPaymentRequestV1,
        @RequestHeader(name = "X-Correlation-Id", required = false) correlationId: String?,
    ): ResponseEntity<Map<String, String>> { // 반환 타입을 Map으로 변경
        val command = request.toCommand(accountId)
        val resultCorrelationId = processAccountPaymentCommandHandler.handle(command, correlationId)
        val responseBody =
            mapOf(
                "status" to "SUCCESS",
                "message" to "결제가 성공적으로 처리되었습니다.",
                "correlationId" to resultCorrelationId,
            )
        return ResponseEntity.ok(responseBody)
    }

    /**
     * 계좌 결제 취소
     */
    @PostMapping("/{accountId}/payments/cancel")
    fun cancelPayment(
        @PathVariable accountId: Long,
        @Valid @RequestBody request: CancelPaymentRequestV1,
        @RequestHeader(name = "X-Correlation-Id", required = false) correlationId: String?,
    ): ResponseEntity<Map<String, String>> { // 반환 타입을 Map으로 변경
        val command = request.toCommand(accountId)
        val resultCorrelationId = cancelAccountPaymentCommandHandler.handle(command, correlationId)
        val responseBody =
            mapOf(
                "status" to "SUCCESS",
                "message" to "결제가 성공적으로 취소되었습니다.",
                "correlationId" to resultCorrelationId,
            )
        return ResponseEntity.ok(responseBody)
    }
}
