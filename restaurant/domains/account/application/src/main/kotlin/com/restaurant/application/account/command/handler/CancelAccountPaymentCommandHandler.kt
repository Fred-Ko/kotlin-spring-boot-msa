package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.CancelAccountPaymentCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toOrderId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.exception.PaymentAlreadyCancelledException
import com.restaurant.domain.account.exception.TransactionNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.TransactionType
import com.restaurant.presentation.account.v1.common.AccountErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 계좌 결제 취소 커맨드 핸들러
 */
@Service
class CancelAccountPaymentCommandHandler(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    /**
     * 계좌 결제 취소 커맨드 처리
     *
     * @param command 계좌 결제 취소 커맨드
     * @param correlationId 요청 추적을 위한 상관관계 ID
     * @return 커맨드 결과
     */
    @Transactional
    fun handle(
        command: CancelAccountPaymentCommand,
        correlationId: String? = null,
    ): CommandResult {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()

        return runCatching {
            val accountId = command.toAccountId()
            val orderId = command.toOrderId()

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountNotFoundException(accountId)

            // 해당 주문의 거래 내역 조회
            val prevTransactions =
                transactionRepository
                    .findByOrderId(orderId)
                    .takeIf { it.isNotEmpty() }
                    ?: throw TransactionNotFoundException(orderId.toString())

            // 결제 취소할 금액 계산 (모든 DEBIT 트랜잭션의 합)
            val refundAmount =
                prevTransactions
                    .filter { it.type == TransactionType.DEBIT }
                    .map { it.amount }
                    .fold(Money.ZERO) { acc, amount -> acc + amount }

            // 이미 취소된 거래인지 확인 (CREDIT 트랜잭션이 있는 경우)
            if (prevTransactions.any { it.type == TransactionType.CREDIT }) {
                throw PaymentAlreadyCancelledException(orderId.toString())
            }

            // 계좌에 금액 추가 및 환불 트랜잭션 생성
            val updatedAccount = account.credit(refundAmount)
            val refundTransaction =
                Transaction.credit(
                    amount = refundAmount,
                    orderId = orderId,
                    accountId = accountId,
                )

            // 업데이트된 계좌 및 환불 트랜잭션 저장
            accountRepository.save(updatedAccount)
            transactionRepository.save(refundTransaction)

            CommandResult.success(correlationId = actualCorrelationId)
        }.getOrElse { e ->
            when (e) {
                is IllegalArgumentException ->
                    CommandResult.fail(
                        correlationId = actualCorrelationId,
                        errorCode = AccountErrorCode.UNKNOWN.code,
                        errorMessage = "유효하지 않은 요청 파라미터입니다: ${e.message}",
                    )
                is AccountNotFoundException ->
                    CommandResult.fail(
                        correlationId = actualCorrelationId,
                        errorCode = AccountErrorCode.ACCOUNT_NOT_FOUND.code,
                        errorMessage = e.message,
                        errorDetails = mapOf("accountId" to e.accountId.value.toString()),
                    )
                is TransactionNotFoundException ->
                    CommandResult.fail(
                        correlationId = actualCorrelationId,
                        errorCode = AccountErrorCode.TRANSACTION_NOT_FOUND.code,
                        errorMessage = e.message,
                        errorDetails = mapOf("orderId" to orderId.value.toString()),
                    )
                is PaymentAlreadyCancelledException ->
                    CommandResult.fail(
                        correlationId = actualCorrelationId,
                        errorCode = AccountErrorCode.PAYMENT_CANCELLATION_FAILED.code,
                        errorMessage = e.message,
                        errorDetails = mapOf("orderId" to orderId.value.toString()),
                    )
                is AccountDomainException ->
                    CommandResult.fail(
                        correlationId = actualCorrelationId,
                        errorCode = AccountErrorCode.PAYMENT_CANCELLATION_FAILED.code,
                        errorMessage = "결제 취소 중 도메인 규칙 위반이 발생했습니다.",
                        errorDetails = mapOf("reason" to (e.message ?: "알 수 없는 도메인 오류")),
                    )
                else ->
                    CommandResult.fail(
                        correlationId = actualCorrelationId,
                        errorCode = AccountErrorCode.PAYMENT_CANCELLATION_FAILED.code,
                        errorMessage = "결제 취소 중 시스템 오류가 발생했습니다.",
                        errorDetails = mapOf("exception" to (e.message ?: "알 수 없는 오류")),
                    )
            }
        }
    }
}
