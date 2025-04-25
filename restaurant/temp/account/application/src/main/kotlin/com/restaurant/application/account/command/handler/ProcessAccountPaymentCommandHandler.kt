package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.ProcessAccountPaymentCommand
import com.restaurant.application.account.exception.AccountApplicationException
import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 계좌 결제 처리 커맨드 핸들러
 */
@Service
class ProcessAccountPaymentCommandHandler(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    private val log = LoggerFactory.getLogger(ProcessAccountPaymentCommandHandler::class.java)

    /**
     * 계좌 결제 처리 명령 처리
     *
     * @param command 계좌 결제 처리 명령
     * @param correlationId 요청 추적용 상관관계 ID
     * @return 처리된 요청의 상관관계 ID
     */
    @Transactional
    fun handle(
        command: ProcessAccountPaymentCommand,
        correlationId: String? = null,
    ): String {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()
        log.info("계좌 결제 처리 명령 시작, correlationId={}", actualCorrelationId)

        try {
            val accountId = AccountId.of(command.accountId)
            val amount = Money.of(command.amount)
            val orderId = OrderId.of(command.orderId)

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountDomainException.Account.NotFound(accountId)

            // 계좌에서 금액 차감
            val updatedAccount = account.debit(amount)

            // 거래 내역 생성
            val transaction =
                Transaction.debit(
                    amount = amount,
                    orderId = orderId,
                    accountId = accountId,
                )

            // 업데이트된 계좌 및 트랜잭션 저장
            accountRepository.save(updatedAccount)
            transactionRepository.save(transaction)

            log.info(
                "계좌 결제 처리 명령 완료, correlationId={}, accountId={}, amount={}, orderId={}",
                actualCorrelationId,
                accountId.value,
                amount.value,
                orderId.value,
            )

            return actualCorrelationId
        } catch (e: IllegalArgumentException) {
            log.error("유효하지 않은 입력값, correlationId={}, error={}", actualCorrelationId, e.message, e)
            throw AccountApplicationException.SystemError(
                errorMessage = "유효하지 않은 요청 파라미터입니다: ${e.message}",
            )
        } catch (e: Exception) {
            log.error(
                "결제 처리 중 시스템 오류, correlationId={}, error={}",
                actualCorrelationId,
                e.message,
                e,
            )
            throw AccountApplicationException.SystemError(
                errorMessage = "결제 처리 중 시스템 오류가 발생했습니다: ${e.message}",
            )
        }
    }
}
