package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.CancelAccountPaymentCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toOrderId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.domain.account.vo.TransactionType
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * 계좌 결제 취소 명령 핸들러
 */
@Component
class CancelAccountPaymentCommandHandler(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    /**
     * 계좌 결제를 취소합니다.
     * 원래 차감된 금액만큼 다시 계좌에 추가합니다.
     */
    fun handle(command: CancelAccountPaymentCommand): CommandResult {
        try {
            val accountId = command.toAccountId()
            val orderId = command.toOrderId()

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountNotFoundException(accountId)

            // 트랜잭션 리포지토리를 통해 주문 ID로 트랜잭션 조회
            val transactions = transactionRepository.findByOrderId(orderId)

            // 트랜잭션이 없으면 취소할 내역이 없음
            if (transactions.isEmpty()) {
                return CommandResult(
                    success = false,
                    errorCode = "TRANSACTION_NOT_FOUND",
                )
            }

            // DEBIT 타입의 트랜잭션 찾기 (출금 트랜잭션)
            val debitTransaction = transactions.firstOrNull { it.type == TransactionType.DEBIT }

            if (debitTransaction == null) {
                return CommandResult(
                    success = false,
                    errorCode = "DEBIT_TRANSACTION_NOT_FOUND",
                )
            }

            // 원래 차감된 금액만큼 다시 계좌에 추가
            val updatedAccount = account.credit(debitTransaction.amount, orderId)

            // 업데이트된 계좌 저장
            accountRepository.save(updatedAccount)

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
                errorCode = "PAYMENT_CANCEL_FAILED",
            )
        }
    }
}
