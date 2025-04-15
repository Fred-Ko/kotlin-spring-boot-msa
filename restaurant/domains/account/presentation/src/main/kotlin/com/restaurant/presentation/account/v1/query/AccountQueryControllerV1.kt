package com.restaurant.presentation.account.v1.query

import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.application.account.query.GetAccountTransactionsQuery
import com.restaurant.application.account.query.dto.AccountBalanceDto
import com.restaurant.application.account.query.dto.CursorPageDto
import com.restaurant.application.account.query.dto.TransactionDto
import com.restaurant.application.account.query.handler.GetAccountBalanceQueryHandler
import com.restaurant.application.account.query.handler.GetAccountTransactionsQueryHandler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 계좌 쿼리 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/accounts")
class AccountQueryControllerV1(
    private val getAccountBalanceQueryHandler: GetAccountBalanceQueryHandler,
    private val getAccountTransactionsQueryHandler: GetAccountTransactionsQueryHandler,
) {
    /**
     * 계좌 잔액 조회
     */
    @GetMapping("/{accountId}/balance")
    fun getAccountBalance(
        @PathVariable accountId: Long,
    ): ResponseEntity<EntityModel<AccountBalanceDto>> {
        val query = GetAccountBalanceQuery(accountId)
        val result = getAccountBalanceQueryHandler.handle(query)

        // HATEOAS 링크 생성
        val selfLink = linkTo(methodOn(this::class.java).getAccountBalance(accountId)).withSelfRel()
        val transactionsLink = linkTo(methodOn(this::class.java).getAccountTransactions(accountId, null, 10)).withRel("transactions")

        // EntityModel 생성
        val entityModel = EntityModel.of(result, selfLink, transactionsLink)

        return ResponseEntity.ok(entityModel)
    }

    /**
     * 계좌 트랜잭션 목록 조회 (커서 기반 페이지네이션)
     */
    @GetMapping("/{accountId}/transactions")
    fun getAccountTransactions(
        @PathVariable accountId: Long,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<CollectionModel<EntityModel<TransactionDto>>> {
        val query =
            GetAccountTransactionsQuery(
                accountId = accountId,
                cursor = cursor,
                limit = limit.coerceIn(1, 100),
            )
        val result: CursorPageDto<TransactionDto> = getAccountTransactionsQueryHandler.handle(query)

        // 개별 TransactionDto를 EntityModel로 변환 (self 링크 추가 가능)
        val transactionModels =
            result.items.map {
                EntityModel.of(it, linkTo(methodOn(this::class.java).getTransactionDetail(accountId, it.id)).withSelfRel())
            }

        // 페이지 링크 생성
        val selfLink = linkTo(methodOn(this::class.java).getAccountTransactions(accountId, cursor, limit)).withSelfRel()
        val links = mutableListOf(selfLink)
        if (result.hasNext) {
            val nextLink = linkTo(methodOn(this::class.java).getAccountTransactions(accountId, result.nextCursor, limit)).withRel("next")
            links.add(nextLink)
        }

        // CollectionModel 생성
        val collectionModel = CollectionModel.of(transactionModels, links)

        return ResponseEntity.ok(collectionModel)
    }

    // TODO: 트랜잭션 상세 조회 엔드포인트 예시 (실제 구현 필요)
    @GetMapping("/{accountId}/transactions/{transactionId}")
    fun getTransactionDetail(
        @PathVariable accountId: Long,
        @PathVariable transactionId: Long,
    ): ResponseEntity<EntityModel<TransactionDto>> {
        // 실제 상세 조회 로직 구현...
        throw UnsupportedOperationException("Not implemented yet")
    }
}
