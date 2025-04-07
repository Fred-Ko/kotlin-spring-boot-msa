package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.DeleteAccountCommand
import com.restaurant.application.account.extensions.toAccountId
import com.restaurant.common.core.command.CommandResult
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.exception.AccountNotFoundException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.presentation.account.v1.common.AccountErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
     * @param correlationId 요청 추적을 위한 상관관계 ID
     * @return 커맨드 결과
     */
    @Transactional
    fun handle(
        command: DeleteAccountCommand,
        correlationId: String? = null,
    ): CommandResult {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()

        try {
            val accountId = command.toAccountId()

            // 계좌 존재 여부 확인
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountNotFoundException(accountId)

            // 계좌 삭제
            accountRepository.delete(accountId)

            return CommandResult.success(correlationId = actualCorrelationId)
        } catch (e: IllegalArgumentException) {
            // 유효하지 않은 AccountId 등의 문제
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.UNKNOWN.code,
                errorMessage = "유효하지 않은 계좌 ID 형식입니다: ${e.message}",
            )
        } catch (e: AccountNotFoundException) {
            // 계좌를 찾을 수 없는 경우
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.ACCOUNT_NOT_FOUND.code,
                errorMessage = e.message,
                errorDetails = mapOf("accountId" to e.accountId.value.toString()),
            )
        } catch (e: AccountDomainException) {
            // 기타 도메인 예외 처리
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.UNKNOWN.code,
                errorMessage = e.message,
                errorDetails = mapOf("exception" to e.javaClass.simpleName),
            )
        } catch (e: Exception) {
            // 기타 예외 처리
            return CommandResult.fail(
                correlationId = actualCorrelationId,
                errorCode = AccountErrorCode.UNKNOWN.code,
                errorMessage = "계좌 삭제 중 시스템 오류가 발생했습니다.",
                errorDetails = mapOf("exception" to (e.message ?: "알 수 없는 오류")),
            )
        }
    }
}
