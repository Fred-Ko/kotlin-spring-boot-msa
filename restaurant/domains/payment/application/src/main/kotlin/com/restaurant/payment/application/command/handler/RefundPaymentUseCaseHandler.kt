package com.restaurant.payment.application.command.handler

import com.restaurant.payment.application.command.dto.RefundPaymentCommand
import com.restaurant.payment.application.command.usecase.RefundPaymentUseCase
import com.restaurant.payment.application.exception.PaymentApplicationException
import com.restaurant.payment.domain.repository.PaymentRepository
import com.restaurant.payment.domain.vo.PaymentId
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * RefundPaymentUseCase 구현체 (Rule 75, 76, 77 적용)
 */
@Service
class RefundPaymentUseCaseHandler(
    private val paymentRepository: PaymentRepository,
) : RefundPaymentUseCase {
    private val log = LoggerFactory.getLogger(RefundPaymentUseCaseHandler::class.java)

    @Transactional
    @Retry(name = "payment-refund")
    @CircuitBreaker(name = "payment-refund", fallbackMethod = "fallbackRefundPayment")
    override fun execute(command: RefundPaymentCommand): String {
        log.info("Processing refund for payment: ${command.paymentId}")

        try {
            // 1. 입력 검증
            validateCommand(command)

            // 2. Payment 조회
            val paymentId = PaymentId.ofString(command.paymentId)
            val payment =
                paymentRepository.findById(paymentId)
                    ?: throw PaymentApplicationException.NotFound.PaymentNotFound(command.paymentId)

            // 3. 환불 처리
            val refundedPayment = payment.refund()
            val savedPayment = paymentRepository.save(refundedPayment)

            log.info(
                "Payment refund processed successfully, paymentId={}, refundAmount={}",
                savedPayment.id,
                command.refundAmount,
            )

            return savedPayment.id.toString()
        } catch (e: PaymentApplicationException) {
            log.error(
                "Payment refund failed, errorCode={}, paymentId={}, error={}",
                e.errorCode.code,
                command.paymentId,
                e.message,
                e,
            )
            throw e
        } catch (e: Exception) {
            log.error(
                "Unexpected error during payment refund, paymentId={}, error={}",
                command.paymentId,
                e.message,
                e,
            )
            throw PaymentApplicationException.System.UnexpectedError(
                "Unexpected error during payment refund",
                e,
            )
        }
    }

    private fun validateCommand(command: RefundPaymentCommand) {
        if (command.paymentId.isBlank()) {
            throw PaymentApplicationException.Validation.InvalidRefundRequest("Payment ID cannot be blank")
        }
        if (command.refundAmount <= java.math.BigDecimal.ZERO) {
            throw PaymentApplicationException.Validation.InvalidRefundRequest("Refund amount must be positive")
        }
    }

    /**
     * Circuit Breaker 폴백 메서드
     */
    private fun fallbackRefundPayment(
        command: RefundPaymentCommand,
        exception: Exception,
    ): String {
        log.error(
            "Payment refund circuit breaker activated, paymentId={}, error={}",
            command.paymentId,
            exception.message,
        )
        throw PaymentApplicationException.ExternalService.PaymentGatewayUnavailable(exception)
    }
}
