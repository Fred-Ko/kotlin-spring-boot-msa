package com.restaurant.payment.application.command.usecase

import com.restaurant.payment.application.command.dto.DeletePaymentMethodCommand

/**
 * Use case interface for deleting payment method
 */
interface DeletePaymentMethodUseCase {
    /**
     * 결제 수단을 삭제합니다.
     */
    fun execute(command: DeletePaymentMethodCommand): String
}
