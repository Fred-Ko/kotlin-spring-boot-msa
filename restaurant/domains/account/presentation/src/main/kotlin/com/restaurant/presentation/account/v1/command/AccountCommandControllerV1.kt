package com.restaurant.presentation.account.v1.command

import com.restaurant.application.account.command.handler.CancelAccountPaymentCommandHandler
import com.restaurant.application.account.command.handler.ProcessAccountPaymentCommandHandler
import com.restaurant.presentation.account.v1.common.AccountErrorCode
import com.restaurant.presentation.account.v1.dto.request.CancelPaymentRequestV1
import com.restaurant.presentation.account.v1.dto.request.ProcessPaymentRequestV1
import com.restaurant.presentation.account.v1.extensions.request.toCommand
import jakarta.validation.Valid
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.Instant

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
    ): ResponseEntity<Any> {
        val command = request.toCommand(accountId)
        val result = processAccountPaymentCommandHandler.handle(command)

        if (!result.success) {
            val errorCode = AccountErrorCode.fromCode(result.errorCode)
            val problem =
                ProblemDetail.forStatus(errorCode.status).apply {
                    type = URI.create("probs/${errorCode.code.lowercase()}")
                    title = errorCode.code
                    detail = errorCode.message
                    instance = URI.create("/api/v1/accounts/$accountId/payments")
                    setProperty("errorCode", errorCode.code)
                    setProperty("timestamp", Instant.now().toString())
                }

            return ResponseEntity.status(errorCode.status).body(problem)
        }

        return ResponseEntity.ok(
            mapOf(
                "status" to "success",
                "message" to "결제가 처리되었습니다.",
                "correlationId" to result.correlationId,
            ),
        )
    }

    /**
     * 계좌 결제 취소
     */
    @PostMapping("/{accountId}/payments/cancel")
    fun cancelPayment(
        @PathVariable accountId: Long,
        @Valid @RequestBody request: CancelPaymentRequestV1,
    ): ResponseEntity<Any> {
        val command = request.toCommand(accountId)
        val result = cancelAccountPaymentCommandHandler.handle(command)

        if (!result.success) {
            val errorCode = AccountErrorCode.fromCode(result.errorCode)
            val problem =
                ProblemDetail.forStatus(errorCode.status).apply {
                    type = URI.create("probs/${errorCode.code.lowercase()}")
                    title = errorCode.code
                    detail = errorCode.message
                    instance = URI.create("/api/v1/accounts/$accountId/payments/cancel")
                    setProperty("errorCode", errorCode.code)
                    setProperty("timestamp", Instant.now().toString())
                }

            return ResponseEntity.status(errorCode.status).body(problem)
        }

        return ResponseEntity.ok(
            mapOf(
                "status" to "success",
                "message" to "결제가 취소되었습니다.",
                "correlationId" to result.correlationId,
            ),
        )
    }
}
