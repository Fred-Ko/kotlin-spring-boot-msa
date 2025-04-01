package com.restaurant.application.account.query.handler

import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.application.account.query.result.AccountBalanceResult
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service

/**
 * 계좌 잔액 조회 쿼리 핸들러
 */
@Service
class GetAccountBalanceQueryHandler(
    private val accountRepository: AccountRepository,
) {
    /**
     * 계좌 잔액 조회 쿼리 처리
     *
     * @param query 계좌 잔액 조회 쿼리
     * @return 계좌 잔액 결과
     * @throws AccountNotFoundException 계좌를 찾을 수 없을 경우
     */
    fun handle(query: GetAccountBalanceQuery): AccountBalanceResult {
        val accountId = query.toAccountId()

        // 계좌 조회
        val account =
            accountRepository.findById(accountId)
                ?: throw AccountNotFoundException(accountId)

        // 잔액 반환
        return AccountBalanceResult(
            accountId = accountId.value,
            balance = account.balance.amount.toLong(),
        )
    }
}
