package com.restaurant.payment.application.command.usecase

import com.restaurant.payment.application.command.dto.RegisterPaymentMethodCommand

/**
 * Use case interface for registering payment method
 */
interface RegisterPaymentMethodUseCase {
    /**
     * 결제 수단을 등록합니다.
     */
    suspend fun execute(command: RegisterPaymentMethodCommand): String
}
