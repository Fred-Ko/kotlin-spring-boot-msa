package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.CancelAccountPaymentCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toOrderId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.domain.account.vo.Money
import org.springframework.stereotype.Service
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
     * @return 커맨드 결과
     */
    fun handle(command: CancelAccountPaymentCommand): CommandResult {
        try {
            val accountId = command.toAccountId()
            val orderId = command.toOrderId()

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountNotFoundException(accountId)

            // 해당 주문의 거래 내역 조회
            val prevTransactions = transactionRepository.findByOrderId(orderId)

            // 이전 결제 트랜잭션이 없으면 실패 반환
            if (prevTransactions.isEmpty()) {
                return CommandResult(
                    success = false,
                    errorCode = "TRANSACTION_NOT_FOUND",
                )
            }

            // 결제 취소할 금액 계산 (모든 DEBIT 트랜잭션의 합)
            val refundAmount =
                prevTransactions
                    .filter { it.type == com.restaurant.domain.account.vo.TransactionType.DEBIT }
                    .map { it.amount }
                    .fold(Money.ZERO) { acc, amount -> acc + amount }

            // 이미 취소된 거래인지 확인 (CREDIT 트랜잭션이 있는 경우)
            val alreadyCancelled =
                prevTransactions.any {
                    it.type == com.restaurant.domain.account.vo.TransactionType.CREDIT
                }

            if (alreadyCancelled) {
                return CommandResult(
                    success = false,
                    errorCode = "PAYMENT_ALREADY_CANCELLED",
                )
            }

            // 계좌에 금액 추가
            val updatedAccount = account.credit(refundAmount)

            // 환불 트랜잭션 생성
            val refundTransaction =
                Transaction.credit(
                    amount = refundAmount,
                    orderId = orderId,
                    accountId = accountId,
                )

            // 업데이트된 계좌 및 환불 트랜잭션 저장
            accountRepository.save(updatedAccount)
            transactionRepository.save(refundTransaction)

            return CommandResult(
                success = true,
                correlationId = UUID.randomUUID().toString(),
            )
        } catch (e: AccountNotFoundException) {
            return CommandResult(
                success = false,
                errorCode = "ACCOUNT_NOT_FOUND",
            )
        } catch (e: Exception) {
            return CommandResult(
                success = false,
                errorCode = "PAYMENT_CANCELLATION_FAILED",
            )
        }
    }
}
