package com.restaurant.application.account.query.handler

import com.restaurant.application.account.exception.AccountApplicationException
import com.restaurant.application.account.exception.AccountNotFoundException
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toCursor
import com.restaurant.application.account.extensions.toDto
import com.restaurant.application.account.query.GetAccountTransactionsQuery
import com.restaurant.application.account.query.dto.CursorPageDto
import com.restaurant.application.account.query.dto.TransactionDto
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.restaurant.domain.account.exception.AccountNotFoundException as DomainAccountNotFoundException

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
    @Transactional(readOnly = true)
    fun handle(query: GetAccountTransactionsQuery): CursorPageDto<TransactionDto> {
        try {
            val accountId = query.toAccountId()
            val cursor = query.toCursor()
            val limit = query.limit + 1 // 다음 페이지 확인을 위해 1개 더 조회

            // 계좌 존재 여부 확인
            accountRepository.findById(accountId)
                ?: throw DomainAccountNotFoundException(accountId)

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
        } catch (e: IllegalArgumentException) {
            // 유효하지 않은 AccountId 등의 문제
            throw AccountApplicationException(
                "잘못된 계좌 ID 또는 커서 형식입니다: ${e.message}",
            )
        } catch (e: DomainAccountNotFoundException) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw AccountNotFoundException(e.accountId)
        } catch (e: AccountDomainException) {
            // 기타 도메인 예외 처리
            throw AccountApplicationException(e.message ?: "계좌 거래내역 조회 중 오류가 발생했습니다.")
        } catch (e: Exception) {
            // 기타 예외 처리
            throw AccountApplicationException("계좌 거래내역 조회 중 시스템 오류가 발생했습니다: ${e.message}")
        }
    }
}
