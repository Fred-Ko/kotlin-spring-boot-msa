package com.restaurant.payment.application.command.handler

import com.restaurant.payment.application.command.dto.UpdatePaymentMethodCommand
import com.restaurant.payment.application.command.usecase.UpdatePaymentMethodUseCase
import com.restaurant.payment.application.exception.PaymentApplicationException
import com.restaurant.payment.domain.repository.PaymentMethodRepository
import com.restaurant.payment.domain.vo.PaymentMethodId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * UpdatePaymentMethodUseCase 구현체 (Rule 75, 76, 77 적용)
 */
@Service
class UpdatePaymentMethodUseCaseHandler(
    private val paymentMethodRepository: PaymentMethodRepository,
) : UpdatePaymentMethodUseCase {
    private val log = LoggerFactory.getLogger(UpdatePaymentMethodUseCaseHandler::class.java)

    @Transactional
    override fun execute(command: UpdatePaymentMethodCommand): String {
        log.info("Updating payment method: ${command.paymentMethodId}")

        try {
            // 1. 입력 검증
            validateCommand(command)

            // 2. PaymentMethod 조회
            val paymentMethodId = PaymentMethodId.ofString(command.paymentMethodId)
            val paymentMethod =
                paymentMethodRepository.findById(paymentMethodId)
                    ?: throw PaymentApplicationException.NotFound.PaymentMethodNotFound(command.paymentMethodId)

            // 3. 업데이트 처리
            var updatedPaymentMethod = paymentMethod

            // 별칭 업데이트
            command.alias?.let { newAlias ->
                updatedPaymentMethod = updatedPaymentMethod.updateAlias(newAlias)
            }

            // 기본 결제 수단 설정
            command.isDefault?.let { isDefault ->
                if (isDefault) {
                    // 기존 기본 결제 수단을 해제
                    paymentMethodRepository.findDefaultByUserId(paymentMethod.userId)?.let { defaultMethod ->
                        if (defaultMethod.paymentMethodId != paymentMethodId) {
                            val nonDefaultMethod = defaultMethod.unsetAsDefault()
                            paymentMethodRepository.save(nonDefaultMethod)
                        }
                    }
                    updatedPaymentMethod = updatedPaymentMethod.setAsDefault()
                } else {
                    updatedPaymentMethod = updatedPaymentMethod.unsetAsDefault()
                }
            }

            val savedPaymentMethod = paymentMethodRepository.save(updatedPaymentMethod)

            log.info(
                "Payment method updated successfully, paymentMethodId={}",
                savedPaymentMethod.paymentMethodId,
            )

            return savedPaymentMethod.paymentMethodId.toString()
        } catch (e: PaymentApplicationException) {
            log.error(
                "Payment method update failed, errorCode={}, paymentMethodId={}, error={}",
                e.errorCode.code,
                command.paymentMethodId,
                e.message,
                e,
            )
            throw e
        } catch (e: Exception) {
            log.error(
                "Unexpected error during payment method update, paymentMethodId={}, error={}",
                command.paymentMethodId,
                e.message,
                e,
            )
            throw PaymentApplicationException.System.UnexpectedError(
                "Unexpected error during payment method update",
                e,
            )
        }
    }

    private fun validateCommand(command: UpdatePaymentMethodCommand) {
        if (command.paymentMethodId.isBlank()) {
            throw PaymentApplicationException.Validation.InvalidPaymentMethodRequest("Payment method ID cannot be blank")
        }

        command.alias?.let { alias ->
            if (alias.isBlank()) {
                throw PaymentApplicationException.Validation.InvalidPaymentMethodRequest("Alias cannot be blank")
            }
        }
    }
}
