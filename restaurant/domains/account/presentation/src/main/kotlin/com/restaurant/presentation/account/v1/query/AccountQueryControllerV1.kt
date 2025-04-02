package com.restaurant.presentation.account.v1.query

import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.application.account.query.handler.GetAccountBalanceQueryHandler
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.presentation.account.v1.extensions.response.toResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

/**
 * 계좌 쿼리 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/accounts")
class AccountQueryControllerV1(
    private val getAccountBalanceQueryHandler: GetAccountBalanceQueryHandler,
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
}
