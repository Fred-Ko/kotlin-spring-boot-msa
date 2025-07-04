package com.restaurant.payment.application.command.handler

import com.restaurant.payment.application.command.dto.DeletePaymentMethodCommand
import com.restaurant.payment.application.command.usecase.DeletePaymentMethodUseCase
import com.restaurant.payment.application.exception.PaymentApplicationException
import com.restaurant.payment.domain.repository.PaymentMethodRepository
import com.restaurant.payment.domain.repository.PaymentRepository
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.PaymentStatus
import com.restaurant.payment.domain.vo.UserId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * DeletePaymentMethodUseCase 구현체 (Rule 75, 76, 77 적용)
 */
@Service
class DeletePaymentMethodUseCaseHandler(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val paymentRepository: PaymentRepository,
) : DeletePaymentMethodUseCase {
    private val log = LoggerFactory.getLogger(DeletePaymentMethodUseCaseHandler::class.java)

    @Transactional
    override fun execute(command: DeletePaymentMethodCommand): String {
        log.info("Deleting payment method: ${command.paymentMethodId}")

        try {
            // 1. 입력 검증
            validateCommand(command)

            // 2. PaymentMethod 조회
            val paymentMethodId = PaymentMethodId.ofString(command.paymentMethodId)
            val paymentMethod =
                paymentMethodRepository.findById(paymentMethodId)
                    ?: throw PaymentApplicationException.NotFound.PaymentMethodNotFound(command.paymentMethodId)

            // 3. 소유권 검증
            val userId = UserId.ofString(command.userId)
            paymentMethod.validateOwnership(userId)

            // 4. 진행 중인 결제가 있는지 확인
            val ongoingPayments =
                paymentRepository
                    .findByUserIdAndStatus(userId, PaymentStatus.PENDING)
                    .filter { it.paymentMethodId == paymentMethodId }

            if (ongoingPayments.isNotEmpty()) {
                throw PaymentApplicationException.NotFound.CannotDeletePaymentMethodWithOngoingPayments(
                    command.paymentMethodId,
                    ongoingPayments.size,
                )
            }

            // 5. 기본 결제 수단인지 확인
            if (paymentMethod.isDefault) {
                val userPaymentMethods = paymentMethodRepository.findByUserId(userId)
                if (userPaymentMethods.size <= 1) {
                    throw PaymentApplicationException.NotFound.CannotDeletePaymentMethodWithOngoingPayments(
                        command.paymentMethodId,
                        0, // 마지막 결제 수단이므로 삭제 불가
                    )
                }

                // 다른 결제 수단을 기본으로 설정
                val otherPaymentMethod = userPaymentMethods.first { it.paymentMethodId != paymentMethodId }
                val newDefaultMethod = otherPaymentMethod.setAsDefault()
                paymentMethodRepository.save(newDefaultMethod)
            }

            // 6. 결제 수단 삭제
            paymentMethodRepository.delete(paymentMethod)

            log.info(
                "Payment method deleted successfully, paymentMethodId={}",
                paymentMethodId,
            )

            return paymentMethodId.toString()
        } catch (e: PaymentApplicationException) {
            log.error(
                "Payment method deletion failed, errorCode={}, paymentMethodId={}, error={}",
                e.errorCode.code,
                command.paymentMethodId,
                e.message,
                e,
            )
            throw e
        } catch (e: Exception) {
            log.error(
                "Unexpected error during payment method deletion, paymentMethodId={}, error={}",
                command.paymentMethodId,
                e.message,
                e,
            )
            throw PaymentApplicationException.System.UnexpectedError(
                "Unexpected error during payment method deletion",
                e,
            )
        }
    }

    private fun validateCommand(command: DeletePaymentMethodCommand) {
        if (command.paymentMethodId.isBlank()) {
            throw PaymentApplicationException.Validation.InvalidPaymentMethodRequest("Payment method ID cannot be blank")
        }
        if (command.userId.isBlank()) {
            throw PaymentApplicationException.Validation.InvalidPaymentMethodRequest("User ID cannot be blank")
        }
    }
}
