package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.DeleteAccountCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * 계좌 삭제 커맨드 핸들러
 */
@Service
class DeleteAccountCommandHandler(
    private val accountRepository: AccountRepository,
) {
    /**
     * 계좌 삭제 커맨드 처리
     *
     * @param command 계좌 삭제 커맨드
     * @return 커맨드 결과
     */
    fun handle(command: DeleteAccountCommand): CommandResult {
        try {
            val accountId = command.toAccountId()

            // 계좌 존재 여부 확인
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountNotFoundException(accountId)

            // 계좌 삭제
            accountRepository.delete(accountId)

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
                errorCode = "DELETE_ACCOUNT_FAILED",
            )
        }
    }
}
