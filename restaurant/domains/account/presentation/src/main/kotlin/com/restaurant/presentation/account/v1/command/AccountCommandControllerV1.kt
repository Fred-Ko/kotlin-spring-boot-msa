package com.restaurant.presentation.account.v1.command

import com.restaurant.application.account.command.handler.CancelAccountPaymentCommandHandler
import com.restaurant.application.account.command.handler.ProcessAccountPaymentCommandHandler
import com.restaurant.presentation.account.v1.dto.request.CancelPaymentRequestV1
import com.restaurant.presentation.account.v1.dto.request.ProcessPaymentRequestV1
import com.restaurant.presentation.account.v1.dto.response.BusinessErrorResponse
import com.restaurant.presentation.account.v1.dto.response.CommandResultResponseV1
import com.restaurant.presentation.account.v1.dto.response.InternalServerErrorResponse
import com.restaurant.presentation.account.v1.dto.response.NotFoundErrorResponse
import com.restaurant.presentation.account.v1.dto.response.ValidationErrorResponse
import com.restaurant.presentation.account.v1.extensions.request.toCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "계좌 커맨드 API",
    description = "계좌 관련 커맨드를 처리하는 API",
)
@RestController
@RequestMapping("/api/v1/accounts")
class AccountCommandControllerV1(
    private val processAccountPaymentCommandHandler: ProcessAccountPaymentCommandHandler,
    private val cancelAccountPaymentCommandHandler: CancelAccountPaymentCommandHandler,
) {
    @Operation(
        summary = "계좌 결제 처리",
        description = "주문에 대한 결제를 처리합니다.",
    )
    @ApiResponse(
        responseCode = "200",
        description = "결제 처리 성공",
        content = [Content(schema = Schema(implementation = CommandResultResponseV1::class))],
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 또는 잔액 부족",
        content = [Content(schema = Schema(implementation = BusinessErrorResponse::class))],
    )
    @ApiResponse(
        responseCode = "404",
        description = "계좌를 찾을 수 없음",
        content = [Content(schema = Schema(implementation = NotFoundErrorResponse::class))],
    )
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = [Content(schema = Schema(implementation = InternalServerErrorResponse::class))],
    )
    @PostMapping("/{accountId}/payments")
    fun processPayment(
        @Parameter(description = "계좌 ID")
        @PathVariable accountId: Long,
        @Valid @RequestBody request: ProcessPaymentRequestV1,
        @Parameter(description = "상관 관계 ID")
        @RequestHeader(name = "X-Correlation-Id", required = false) correlationId: String?,
    ): ResponseEntity<CommandResultResponseV1> {
        val command = request.toCommand(accountId)
        val resultCorrelationId =
            processAccountPaymentCommandHandler.handle(
                command,
                correlationId,
            )

        val response =
            CommandResultResponseV1(
                status = "SUCCESS",
                message = "결제가 성공적으로 처리되었습니다.",
                correlationId = resultCorrelationId,
            )

        response.add(
            linkTo<AccountCommandControllerV1> {
                this.processPayment(accountId, request, correlationId)
            }.withSelfRel(),
        )

        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "계좌 결제 취소",
        description = "처리된 결제를 취소합니다.",
    )
    @ApiResponse(
        responseCode = "200",
        description = "결제 취소 성공",
        content = [Content(schema = Schema(implementation = CommandResultResponseV1::class))],
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 또는 이미 취소된 결제",
        content = [Content(schema = Schema(implementation = ValidationErrorResponse::class))],
    )
    @ApiResponse(
        responseCode = "404",
        description = "계좌 또는 거래내역을 찾을 수 없음",
        content = [Content(schema = Schema(implementation = NotFoundErrorResponse::class))],
    )
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = [Content(schema = Schema(implementation = InternalServerErrorResponse::class))],
    )
    @PostMapping("/{accountId}/payments/cancel")
    fun cancelPayment(
        @Parameter(description = "계좌 ID")
        @PathVariable accountId: Long,
        @Valid @RequestBody request: CancelPaymentRequestV1,
        @Parameter(description = "상관 관계 ID")
        @RequestHeader(name = "X-Correlation-Id", required = false) correlationId: String?,
    ): ResponseEntity<CommandResultResponseV1> {
        val command = request.toCommand(accountId)
        val resultCorrelationId =
            cancelAccountPaymentCommandHandler.handle(
                command,
                correlationId,
            )

        val response =
            CommandResultResponseV1(
                status = "SUCCESS",
                message = "결제가 성공적으로 취소되었습니다.",
                correlationId = resultCorrelationId,
            )

        response.add(
            linkTo<AccountCommandControllerV1> {
                this.cancelPayment(accountId, request, correlationId)
            }.withSelfRel(),
        )

        return ResponseEntity.ok(response)
    }
}
