package com.restaurant.application.account.query.handler

import com.restaurant.application.account.exception.AccountApplicationException
import com.restaurant.application.account.exception.AccountNotFoundException
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.application.account.query.dto.AccountBalanceDto
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.restaurant.domain.account.exception.AccountNotFoundException as DomainAccountNotFoundException

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
    @Transactional(readOnly = true)
    fun handle(query: GetAccountBalanceQuery): AccountBalanceDto {
        try {
            val accountId = query.toAccountId()

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw DomainAccountNotFoundException(accountId)

            // 잔액 반환
            return AccountBalanceDto(
                accountId = accountId.value,
                balance = account.balance.value,
            )
        } catch (e: IllegalArgumentException) {
            // 유효하지 않은 AccountId 등의 문제
            throw AccountApplicationException(
                "잘못된 계좌 ID 형식입니다: ${e.message}",
            )
        } catch (e: DomainAccountNotFoundException) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw AccountNotFoundException(e.accountId)
        } catch (e: AccountDomainException) {
            // 기타 도메인 예외 처리
            throw AccountApplicationException(e.message ?: "계좌 조회 중 오류가 발생했습니다.")
        } catch (e: Exception) {
            // 기타 예외 처리
            throw AccountApplicationException("계좌 조회 중 시스템 오류가 발생했습니다: ${e.message}")
        }
    }
}
