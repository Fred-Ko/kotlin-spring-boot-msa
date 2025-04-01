package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.RegisterAccountCommand
import com.restaurant.application.account.extensions.toInitialBalance
import com.restaurant.application.account.extensions.toUserId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service
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
     * @return 커맨드 결과
     */
    fun handle(command: RegisterAccountCommand): CommandResult {
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

            return CommandResult(
                success = true,
                correlationId = UUID.randomUUID().toString(),
            )
        } catch (e: Exception) {
            return CommandResult(
                success = false,
                errorCode = "REGISTER_ACCOUNT_FAILED",
            )
        }
    }
}
