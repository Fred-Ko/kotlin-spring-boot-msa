package com.restaurant.presentation.account.v1.query

import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.application.account.query.GetAccountTransactionsQuery
import com.restaurant.application.account.query.dto.AccountBalanceDto
import com.restaurant.application.account.query.dto.CursorPageDto
import com.restaurant.application.account.query.dto.TransactionDto
import com.restaurant.application.account.query.handler.GetAccountBalanceQueryHandler
import com.restaurant.application.account.query.handler.GetAccountTransactionsQueryHandler
import com.restaurant.presentation.account.v1.dto.response.InternalServerErrorResponse
import com.restaurant.presentation.account.v1.dto.response.NotFoundErrorResponse
import com.restaurant.presentation.account.v1.dto.response.ValidationErrorResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "계좌 쿼리 API",
    description = "계좌 관련 정보를 조회하는 API",
)
@RestController
@RequestMapping("/api/v1/accounts")
@Validated
class AccountQueryControllerV1(
    private val getAccountBalanceQueryHandler: GetAccountBalanceQueryHandler,
    private val getAccountTransactionsQueryHandler: GetAccountTransactionsQueryHandler,
) {
    @Operation(
        summary = "계좌 잔액 조회",
        description = "계좌의 현재 잔액을 조회합니다.",
    )
    @ApiResponse(
        responseCode = "200",
        description = "잔액 조회 성공",
        content = [Content(schema = Schema(implementation = AccountBalanceDto::class))],
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
    @GetMapping("/{accountId}/balance")
    fun getAccountBalance(
        @Parameter(description = "계좌 ID")
        @PathVariable accountId: Long,
    ): ResponseEntity<EntityModel<AccountBalanceDto>> {
        val query = GetAccountBalanceQuery(accountId)
        val result = getAccountBalanceQueryHandler.handle(query)

        // HATEOAS 링크 생성
        val selfLink = linkTo(methodOn(this::class.java).getAccountBalance(accountId)).withSelfRel()
        val transactionsLink =
            linkTo(methodOn(this::class.java).getAccountTransactions(accountId, null, 10))
                .withRel("transactions")

        // EntityModel 생성
        val entityModel =
            EntityModel.of(
                result,
                selfLink,
                transactionsLink,
            )

        return ResponseEntity.ok(entityModel)
    }

    @Operation(
        summary = "계좌 트랜잭션 목록 조회",
        description = "계좌의 트랜잭션 내역을 커서 기반 페이지네이션으로 조회합니다.",
    )
    @ApiResponse(
        responseCode = "200",
        description = "트랜잭션 목록 조회 성공",
        content = [Content(schema = Schema(implementation = TransactionDto::class))],
    )
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 파라미터",
        content = [Content(schema = Schema(implementation = ValidationErrorResponse::class))],
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
    @GetMapping("/{accountId}/transactions")
    fun getAccountTransactions(
        @Parameter(description = "계좌 ID")
        @PathVariable accountId: Long,
        @Parameter(description = "페이지네이션 커서")
        @RequestParam(required = false) cursor: String?,
        @Parameter(description = "한 페이지당 항목 수 (1-100)")
        @Min(1)
        @Max(100)
        @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> {
        val query =
            GetAccountTransactionsQuery(
                accountId = accountId,
                cursor = cursor,
                limit = limit,
            )
        val result: CursorPageDto<TransactionDto> = getAccountTransactionsQueryHandler.handle(query)

        // 개별 TransactionDto를 EntityModel로 변환
        val transactionModels =
            result.items.map { transaction ->
                EntityModel.of(
                    transaction,
                    linkTo(methodOn(this::class.java).getTransactionDetail(accountId, transaction.id))
                        .withSelfRel(),
                )
            }

        // 페이지 링크 생성
        val selfLink =
            linkTo(methodOn(this::class.java).getAccountTransactions(accountId, cursor, limit))
                .withSelfRel()
        val links = mutableListOf(selfLink)
        if (result.hasNext) {
            val nextLink =
                linkTo(methodOn(this::class.java).getAccountTransactions(accountId, result.nextCursor, limit))
                    .withRel("next")
            links.add(nextLink)
        }

        // CollectionModel 생성
        val collectionModel =
            CollectionModel.of(
                transactionModels,
                links,
            )

        return ResponseEntity.ok(collectionModel)
    }

    @Operation(
        summary = "트랜잭션 상세 조회",
        description = "특정 트랜잭션의 상세 정보를 조회합니다.",
    )
    @ApiResponse(
        responseCode = "200",
        description = "트랜잭션 상세 조회 성공",
        content = [Content(schema = Schema(implementation = TransactionDto::class))],
    )
    @ApiResponse(
        responseCode = "404",
        description = "트랜잭션을 찾을 수 없음",
        content = [Content(schema = Schema(implementation = NotFoundErrorResponse::class))],
    )
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content = [Content(schema = Schema(implementation = InternalServerErrorResponse::class))],
    )
    @GetMapping("/{accountId}/transactions/{transactionId}")
    fun getTransactionDetail(
        @Parameter(description = "계좌 ID")
        @PathVariable accountId: Long,
        @Parameter(description = "트랜잭션 ID")
        @PathVariable transactionId: Long,
    ): ResponseEntity<EntityModel<TransactionDto>> {
        // 실제 상세 조회 로직 구현...
        throw UnsupportedOperationException("Not implemented yet")
    }
}
