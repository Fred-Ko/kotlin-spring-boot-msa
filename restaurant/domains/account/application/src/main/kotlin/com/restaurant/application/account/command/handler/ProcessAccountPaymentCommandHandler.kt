package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.ProcessAccountPaymentCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.application.account.extensions.toAmount
import com.restaurant.application.account.extensions.toOrderId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.exception.InsufficientBalanceException
import com.restaurant.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * 계좌 결제 처리 커맨드 핸들러
 */
@Service
class ProcessAccountPaymentCommandHandler(
    private val accountRepository: AccountRepository,
) {
    /**
     * 계좌 결제 처리 커맨드 처리
     *
     * @param command 계좌 결제 처리 커맨드
     * @return 커맨드 결과
     */
    fun handle(command: ProcessAccountPaymentCommand): CommandResult {
        try {
            val accountId = command.toAccountId()
            val amount = command.toAmount()
            val orderId = command.toOrderId()

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountNotFoundException(accountId)

            // 계좌에서 금액 차감
            val updatedAccount = account.debit(amount, orderId)

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
        } catch (e: InsufficientBalanceException) {
            return CommandResult(
                success = false,
                errorCode = "INSUFFICIENT_BALANCE",
            )
        } catch (e: Exception) {
            return CommandResult(
                success = false,
                errorCode = "PAYMENT_PROCESSING_FAILED",
            )
        }
    }
}
