package com.restaurant.payment.application.command.usecase

import com.restaurant.payment.application.command.dto.UpdatePaymentMethodCommand

/**
 * Use case interface for updating payment method
 */
interface UpdatePaymentMethodUseCase {
    /**
     * 결제 수단을 업데이트합니다.
     */
    fun execute(command: UpdatePaymentMethodCommand): String
}
