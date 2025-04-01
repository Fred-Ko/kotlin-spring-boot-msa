package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.CancelAccountPaymentCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toOrderId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * 계좌 결제 취소 커맨드 핸들러
 */
@Service
class CancelAccountPaymentCommandHandler(
    private val accountRepository: AccountRepository,
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

            // 원래 트랜잭션 찾기
            val transaction = account.findTransactionByOrderId(orderId)

            // 트랜잭션이 없으면 취소할 내역이 없음
            if (transaction == null) {
                return CommandResult(
                    success = false,
                    errorCode = "TRANSACTION_NOT_FOUND",
                )
            }

            // 원래 차감된 금액만큼 다시 계좌에 추가
            val updatedAccount = account.credit(transaction.amount, orderId)

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
