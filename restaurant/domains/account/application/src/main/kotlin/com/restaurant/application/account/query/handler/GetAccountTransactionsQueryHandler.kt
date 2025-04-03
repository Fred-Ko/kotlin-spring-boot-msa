package com.restaurant.application.account.query.handler

import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toCursor
import com.restaurant.application.account.extensions.toDto
import com.restaurant.application.account.query.GetAccountTransactionsQuery
import com.restaurant.application.account.query.dto.CursorPageDto
import com.restaurant.application.account.query.dto.TransactionDto
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import org.springframework.stereotype.Service

/**
 * 계좌 트랜잭션 조회 핸들러
 */
@Service
class GetAccountTransactionsQueryHandler(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    /**
     * 계좌 트랜잭션 조회 쿼리 처리
     *
     * @param query 트랜잭션 조회 쿼리
     * @return 커서 기반 트랜잭션 목록
     * @throws AccountNotFoundException 계좌를 찾을 수 없을 경우
     */
    fun handle(query: GetAccountTransactionsQuery): CursorPageDto<TransactionDto> {
        val accountId = query.toAccountId()
        val cursor = query.toCursor()
        val limit = query.limit + 1 // 다음 페이지 확인을 위해 1개 더 조회

        // 계좌 존재 여부 확인
        accountRepository.findById(accountId) ?: throw AccountNotFoundException(accountId)

        // 트랜잭션 조회
        val transactions =
            transactionRepository.findByAccountIdWithCursor(
                accountId = accountId,
                cursor = cursor,
                limit = limit,
            )

        // 다음 페이지 존재 여부 확인
        val hasNext = transactions.size > query.limit
        val items = transactions.take(query.limit).map { it.toDto() }

        // 다음 커서 설정
        val nextCursor =
            if (hasNext && items.isNotEmpty()) {
                items.last().id.toString()
            } else {
                null
            }

        return CursorPageDto(
            items = items,
            nextCursor = nextCursor,
            hasNext = hasNext,
        )
    }
}
