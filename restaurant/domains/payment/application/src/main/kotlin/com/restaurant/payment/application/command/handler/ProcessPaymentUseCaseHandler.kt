package com.restaurant.payment.application.command.handler

import com.restaurant.payment.application.command.dto.ProcessPaymentCommand
import com.restaurant.payment.application.command.usecase.ProcessPaymentUseCase
import com.restaurant.payment.application.exception.PaymentApplicationException
import com.restaurant.payment.domain.aggregate.Payment
import com.restaurant.payment.domain.repository.PaymentRepository
import com.restaurant.payment.domain.vo.Amount
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.UserId
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * ProcessPaymentUseCase 구현체 (Rule 75, 76, 77 적용)
 */
@Service
class ProcessPaymentUseCaseHandler(
    private val paymentRepository: PaymentRepository,
) : ProcessPaymentUseCase {
    private val log = LoggerFactory.getLogger(ProcessPaymentUseCaseHandler::class.java)

    @Transactional
    @Retry(name = "payment-processing")
    @CircuitBreaker(name = "payment-processing", fallbackMethod = "fallbackProcessPayment")
    override suspend fun execute(command: ProcessPaymentCommand): String {
        log.info("Processing payment for order: ${command.orderId}, user: ${command.userId}")

        try {
            // 1. Command 검증
            validateCommand(command)

            // 2. 기존 결제 중복 확인
            val orderId = OrderId.ofString(command.orderId)
            val existingPayment = paymentRepository.findByOrderId(orderId)
            if (existingPayment != null) {
                log.warn("Payment already exists for order: ${command.orderId}")
                throw PaymentApplicationException.Validation.InvalidPaymentRequest(
                    "Payment already exists for order: ${command.orderId}",
                )
            }

            // 3. Payment Aggregate 생성
            val paymentId = PaymentId.generate()
            val userId = UserId.ofString(command.userId)
            val paymentMethodId = PaymentMethodId.ofString(command.paymentMethodId)
            val amount = Amount.of(command.amount)

            val payment =
                Payment.create(
                    id = paymentId,
                    orderId = orderId,
                    userId = userId,
                    paymentMethodId = paymentMethodId,
                    amount = amount,
                )

            // 4. Payment 저장 (Domain Event 발행 포함)
            val savedPayment = paymentRepository.save(payment)

            log.info(
                "Payment processing initiated successfully, paymentId={}, orderId={}, userId={}",
                savedPayment.id,
                command.orderId,
                command.userId,
            )

            return savedPayment.id.toString()
        } catch (e: PaymentApplicationException) {
            log.error(
                "Payment processing failed, errorCode={}, orderId={}, userId={}, error={}",
                e.errorCode.code,
                command.orderId,
                command.userId,
                e.message,
                e,
            )
            throw e
        } catch (e: Exception) {
            log.error(
                "Unexpected error during payment processing, orderId={}, userId={}, error={}",
                command.orderId,
                command.userId,
                e.message,
                e,
            )
            throw PaymentApplicationException.System.UnexpectedError(
                "Unexpected error during payment processing",
                e,
            )
        }
    }

    private fun validateCommand(command: ProcessPaymentCommand) {
        if (command.orderId.isBlank()) {
            throw PaymentApplicationException.Validation.InvalidPaymentRequest("Order ID cannot be blank")
        }
        if (command.userId.isBlank()) {
            throw PaymentApplicationException.Validation.InvalidPaymentRequest("User ID cannot be blank")
        }
        if (command.paymentMethodId.isBlank()) {
            throw PaymentApplicationException.Validation.InvalidPaymentRequest("Payment method ID cannot be blank")
        }
        if (command.amount <= java.math.BigDecimal.ZERO) {
            throw PaymentApplicationException.Validation.InvalidPaymentRequest("Amount must be positive")
        }
    }

    /**
     * Circuit Breaker 폴백 메서드
     */
    private suspend fun fallbackProcessPayment(
        command: ProcessPaymentCommand,
        exception: Exception,
    ): String {
        log.error(
            "Payment processing circuit breaker activated, orderId={}, userId={}, error={}",
            command.orderId,
            command.userId,
            exception.message,
        )
        throw PaymentApplicationException.ExternalService.PaymentGatewayUnavailable(exception)
    }
}
