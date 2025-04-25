package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.DeleteAccountCommand
import com.restaurant.application.account.exception.AccountApplicationException
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.vo.AccountId
import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(DeleteAccountCommandHandler::class.java)

    /**
     * 계좌 삭제 명령 처리
     *
     * @param command 계좌 삭제 명령
     * @param correlationId 요청 추적용 상관관계 ID
     */
    @Transactional
    fun handle(
        command: DeleteAccountCommand,
        correlationId: String? = null,
    ) {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()
        log.info("계좌 삭제 명령 처리 시작, correlationId={}", actualCorrelationId)

        try {
            val accountId = AccountId.of(command.accountId)

            // 계좌 존재 여부 확인
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountDomainException.Account.NotFound(accountId)

            // 계좌 삭제
            accountRepository.delete(accountId)

            log.info(
                "계좌 삭제 명령 처리 완료, correlationId={}, accountId={}",
                actualCorrelationId,
                accountId.value,
            )
        } catch (e: IllegalArgumentException) {
            log.error("유효하지 않은 입력값, correlationId={}, error={}", actualCorrelationId, e.message, e)
            throw AccountApplicationException.SystemError(
                errorMessage = "유효하지 않은 계좌 ID 형식입니다: ${e.message}",
            )
        } catch (e: AccountDomainException.Account.NotFound) {
            log.error(
                "계좌를 찾을 수 없음, correlationId={}, accountId={}",
                actualCorrelationId,
                e.accountId.value,
                e,
            )
            throw AccountApplicationException.Query.NotFound(e.accountId.value.toString())
        } catch (e: Exception) {
            log.error(
                "계좌 삭제 중 시스템 오류, correlationId={}, error={}",
                actualCorrelationId,
                e.message,
                e,
            )
            throw AccountApplicationException.SystemError(
                errorMessage = "계좌 삭제 중 시스템 오류가 발생했습니다: ${e.message}",
            )
        }
    }
}
