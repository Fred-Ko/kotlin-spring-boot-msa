package com.restaurant.payment.application.command.handler

import com.restaurant.payment.application.command.dto.ProcessPaymentCommand
import com.restaurant.payment.application.command.usecase.ProcessPaymentUseCase
import com.restaurant.payment.application.exception.PaymentApplicationException
import com.restaurant.payment.domain.aggregate.Payment
import com.restaurant.payment.domain.repository.PaymentMethodRepository
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
    private val paymentMethodRepository: PaymentMethodRepository,
) : ProcessPaymentUseCase {
    private val log = LoggerFactory.getLogger(ProcessPaymentUseCaseHandler::class.java)

    @Transactional
    @Retry(name = "payment-processing")
    @CircuitBreaker(name = "payment-processing", fallbackMethod = "fallbackProcessPayment")
    override fun execute(command: ProcessPaymentCommand): String {
        log.info("Processing payment for order: ${command.orderId}, user: ${command.userId}")

        try {
            // 1. 입력 검증
            validateCommand(command)

            // 2. PaymentMethod 존재 및 유효성 검증
            val paymentMethodId = PaymentMethodId.ofString(command.paymentMethodId)
            val paymentMethod =
                paymentMethodRepository.findById(paymentMethodId)
                    ?: throw PaymentApplicationException.NotFound.PaymentMethodNotFound(command.paymentMethodId)

            // 3. PaymentMethod 소유권 검증
            val userId = UserId.ofString(command.userId)
            if (paymentMethod.userId != userId) {
                throw PaymentApplicationException.NotFound.PaymentMethodOwnershipMismatch(
                    command.paymentMethodId,
                    command.userId,
                )
            }

            // 4. PaymentMethod 만료 검증 (CreditCard인 경우)
            if (paymentMethod is com.restaurant.payment.domain.aggregate.CreditCard && paymentMethod.isExpired()) {
                throw PaymentApplicationException.NotFound.PaymentMethodExpired(command.paymentMethodId)
            }

            // 5. Payment 생성
            val payment =
                Payment.create(
                    paymentId = PaymentId.generate(),
                    orderId = OrderId.ofString(command.orderId),
                    userId = userId,
                    amount = Amount.of(command.amount),
                    paymentMethodId = paymentMethodId,
                )

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
    private fun fallbackProcessPayment(
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
