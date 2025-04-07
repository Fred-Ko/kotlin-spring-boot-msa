package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.ProcessAccountPaymentCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toAmount
import com.restaurant.application.account.extensions.toOrderId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.exception.InsufficientBalanceException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.presentation.account.v1.common.AccountErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 계좌 결제 처리 커맨드 핸들러
 */
@Service
class ProcessAccountPaymentCommandHandler(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    /**
     * 계좌 결제 처리 커맨드 처리
     *
     * @param command 계좌 결제 처리 커맨드
     * @param correlationId 요청 추적을 위한 상관관계 ID
     * @return 커맨드 결과
     */
    @Transactional
    fun handle(
        command: ProcessAccountPaymentCommand,
        correlationId: String? = null,
    ): CommandResult {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()

        try {
            val accountId = command.toAccountId()
            val amount = command.toAmount()
            val orderId = command.toOrderId()

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountNotFoundException(accountId)

            // 계좌에서 금액 차감
            val updatedAccount = account.debit(amount)

            // 거래 내역 생성
            val transaction =
                Transaction.debit(
                    amount = amount,
                    orderId = orderId,
                    accountId = accountId,
                )

            // 업데이트된 계좌 및 트랜잭션 저장
            accountRepository.save(updatedAccount)
            transactionRepository.save(transaction)

            return CommandResult.success(correlationId = actualCorrelationId)
        } catch (e: IllegalArgumentException) {
            // 유효하지 않은 파라미터
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.UNKNOWN.code,
                errorMessage = "유효하지 않은 요청 파라미터입니다: ${e.message}",
            )
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
            // 기타 도메인 예외
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.PAYMENT_PROCESSING_FAILED.code,
                errorMessage = e.message,
                errorDetails = mapOf("exception" to e.javaClass.simpleName),
            )
        } catch (e: Exception) {
            // 기타 예외 처리
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.PAYMENT_PROCESSING_FAILED.code,
                errorMessage = "결제 처리 중 시스템 오류가 발생했습니다.",
                errorDetails = mapOf("exception" to (e.message ?: "알 수 없는 오류")),
            )
        }
    }
}
