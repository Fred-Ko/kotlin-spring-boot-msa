package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.RegisterAccountCommand
import com.restaurant.application.account.exception.AccountApplicationException
import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 계좌 등록 커맨드 핸들러
 */
@Service
class RegisterAccountCommandHandler(
    private val accountRepository: AccountRepository,
) {
    private val log = LoggerFactory.getLogger(RegisterAccountCommandHandler::class.java)

    /**
     * 계좌 등록 명령 처리
     *
     * @param command 계좌 등록 명령
     * @param correlationId 요청 추적용 상관관계 ID
     */
    @Transactional
    fun handle(
        command: RegisterAccountCommand,
        correlationId: String? = null,
    ) {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()
        log.info("계좌 등록 명령 처리 시작, correlationId={}", actualCorrelationId)

        try {
            val userId = UserId.of(command.userId)
            val initialBalance = Money.of(command.initialBalance)

            // 이미 존재하는 계좌가 있는지 확인
            val existingAccount = accountRepository.findByUserId(userId)
            if (existingAccount != null) {
                // 이미 계좌가 있으면 기존 계좌에 입금만 처리
                val updatedAccount = existingAccount.deposit(initialBalance)
                accountRepository.save(updatedAccount)
                log.info(
                    "기존 계좌에 입금 처리 완료, correlationId={}, userId={}, amount={}",
                    actualCorrelationId,
                    userId.value,
                    initialBalance.value,
                )
            } else {
                // 새 계좌 생성
                val account = Account.create(userId, initialBalance)
                accountRepository.save(account)
                log.info(
                    "새 계좌 생성 완료, correlationId={}, userId={}, initialBalance={}",
                    actualCorrelationId,
                    userId.value,
                    initialBalance.value,
                )
            }
        } catch (e: IllegalArgumentException) {
            log.error("유효하지 않은 입력값, correlationId={}, error={}", actualCorrelationId, e.message, e)
            throw AccountApplicationException.SystemError(
                errorMessage = "유효하지 않은 요청 파라미터입니다: ${e.message}",
            )
        } catch (e: Exception) {
            log.error(
                "계좌 등록 중 시스템 오류, correlationId={}, error={}",
                actualCorrelationId,
                e.message,
                e,
            )
            throw AccountApplicationException.SystemError(
                errorMessage = "계좌 등록 중 시스템 오류가 발생했습니다: ${e.message}",
            )
        }
    }
}
