package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.RegisterAccountCommand
import com.restaurant.application.account.extensions.toInitialBalance
import com.restaurant.application.account.extensions.toUserId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.exception.InsufficientBalanceException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.presentation.account.v1.common.AccountErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 계좌 등록 커맨드 핸들러
 */
@Service
class RegisterAccountCommandHandler(
    private val accountRepository: AccountRepository,
) {
    /**
     * 계좌 등록 커맨드 처리
     *
     * @param command 계좌 등록 커맨드
     * @param correlationId 요청 추적을 위한 상관관계 ID
     * @return 커맨드 결과
     */
    @Transactional
    fun handle(
        command: RegisterAccountCommand,
        correlationId: String? = null,
    ): CommandResult {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()

        try {
            val userId = command.toUserId()
            val initialBalance = command.toInitialBalance()

            // 이미 존재하는 계좌가 있는지 확인
            val existingAccount = accountRepository.findByUserId(userId)
            if (existingAccount != null) {
                // 이미 계좌가 있으면 기존 계좌에 입금만 처리
                val updatedAccount = existingAccount.deposit(initialBalance)
                accountRepository.save(updatedAccount)
            } else {
                // 새 계좌 생성
                val account = Account.create(userId, initialBalance)
                accountRepository.save(account)
            }

            return CommandResult.success(correlationId = actualCorrelationId)
        } catch (e: AccountNotFoundException) {
            // 계좌를 찾을 수 없는 경우
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.ACCOUNT_NOT_FOUND.code,
                errorMessage = e.message,
                errorDetails = mapOf("accountId" to e.accountId.value.toString()),
            )
        } catch (e: InsufficientBalanceException) {
            // 잔액 부족 예외
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.INSUFFICIENT_BALANCE.code,
                errorMessage = e.message,
                errorDetails =
                    mapOf(
                        "accountId" to e.accountId.value.toString(),
                        "balance" to e.balance.value.toString(),
                        "requested" to e.requested.value.toString(),
                    ),
            )
        } catch (e: AccountDomainException) {
            // 기타 도메인 예외 처리
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.PAYMENT_PROCESSING_FAILED.code,
                errorMessage = e.message,
                errorDetails = mapOf("exception" to e.javaClass.simpleName),
            )
        } catch (e: IllegalArgumentException) {
            // 입력값 유효성 검증 실패
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.UNKNOWN.code,
                errorMessage = e.message ?: "유효하지 않은 입력값입니다.",
            )
        } catch (e: Exception) {
            // 기타 예외 처리
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.UNKNOWN.code,
                errorMessage = "계좌 등록 중 시스템 오류가 발생했습니다.",
                errorDetails = mapOf("exception" to (e.message ?: "알 수 없는 오류")),
            )
        }
    }
}
