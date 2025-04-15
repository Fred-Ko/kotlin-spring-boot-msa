package com.restaurant.application.account.command.handler

import com.restaurant.application.account.command.CancelAccountPaymentCommand
import com.restaurant.application.account.exception.AccountApplicationException
import com.restaurant.domain.account.exception.AccountDomainException
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 계좌 결제 취소 커맨드 핸들러
 */
@Service
class CancelAccountPaymentCommandHandler(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    private val log = LoggerFactory.getLogger(CancelAccountPaymentCommandHandler::class.java)

    /**
     * 계좌 결제 취소 명령 처리
     *
     * @param command 계좌 결제 취소 명령
     * @param correlationId 요청 추적용 상관관계 ID
     * @return 처리된 요청의 상관관계 ID
     */
    @Transactional
    fun handle(
        command: CancelAccountPaymentCommand,
        correlationId: String? = null,
    ): String {
        val actualCorrelationId = correlationId ?: UUID.randomUUID().toString()
        log.info("계좌 결제 취소 명령 처리 시작, correlationId={}", actualCorrelationId)

        try {
            val accountId = AccountId.of(command.accountId)
            val orderId = OrderId.of(command.orderId)

            // 계좌 조회
            val account =
                accountRepository.findById(accountId)
                    ?: throw AccountDomainException.Account.NotFound(accountId)

            // 해당 주문과 관련된 트랜잭션 조회
            val transactions = transactionRepository.findByOrderId(orderId)
            if (transactions.isEmpty()) {
                throw AccountDomainException.Transaction.NotFound(
                    transactionId = TransactionId.of(0L),
                )
            }

            // 결제 트랜잭션 찾기 (DEBIT 타입)
            val debitTransaction =
                transactions.firstOrNull { it.type.name == "DEBIT" }
                    ?: throw AccountDomainException.Transaction.NotFound(
                        transactionId = TransactionId.of(0L),
                    )

            // 이미 취소된 트랜잭션인지 확인
            if (debitTransaction.isCancelled()) {
                throw AccountDomainException.Transaction.AlreadyCancelled(debitTransaction.id!!)
            }

            // 결제 취소 처리 (계좌에 금액 반환)
            val updatedAccount = account.credit(debitTransaction.amount)

            // 트랜잭션 취소 처리
            val cancelledTransaction = debitTransaction.cancel()

            // 업데이트된 계좌 및 트랜잭션 저장
            accountRepository.save(updatedAccount)
            transactionRepository.save(cancelledTransaction)

            log.info(
                "계좌 결제 취소 명령 처리 완료, correlationId={}, accountId={}, orderId={}",
                actualCorrelationId,
                accountId.value,
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
                "결제 취소 중 시스템 오류, correlationId={}, error={}",
                actualCorrelationId,
                e.message,
                e,
            )
            throw AccountApplicationException.SystemError(
                errorMessage = "결제 취소 처리 중 시스템 오류가 발생했습니다: ${e.message}",
            )
        }
    }
}
