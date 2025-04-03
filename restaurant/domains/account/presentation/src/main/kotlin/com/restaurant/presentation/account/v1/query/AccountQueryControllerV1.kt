package com.restaurant.presentation.account.v1.query

import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.application.account.query.GetAccountTransactionsQuery
import com.restaurant.application.account.query.handler.GetAccountBalanceQueryHandler
import com.restaurant.application.account.query.handler.GetAccountTransactionsQueryHandler
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.presentation.account.v1.extensions.response.toResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

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
    ): ResponseEntity<Any> {
        try {
            val query = GetAccountBalanceQuery(accountId)
            val result = getAccountBalanceQueryHandler.handle(query)
            return ResponseEntity.ok(result.toResponse())
        } catch (e: AccountNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다.", e)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "계좌 잔액 조회 중 오류가 발생했습니다.", e)
        }
    }

    /**
     * 계좌 트랜잭션 목록 조회 (커서 기반 페이지네이션)
     */
    @GetMapping("/{accountId}/transactions")
    fun getAccountTransactions(
        @PathVariable accountId: Long,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "10") limit: Int,
    ): ResponseEntity<Any> {
        try {
            val query =
                GetAccountTransactionsQuery(
                    accountId = accountId,
                    cursor = cursor,
                    limit = limit.coerceIn(1, 100), // 최소 1개, 최대 100개로 제한
                )
            val result = getAccountTransactionsQueryHandler.handle(query)
            return ResponseEntity.ok(result.toResponse())
        } catch (e: AccountNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다.", e)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "트랜잭션 조회 중 오류가 발생했습니다.", e)
        }
    }
}
