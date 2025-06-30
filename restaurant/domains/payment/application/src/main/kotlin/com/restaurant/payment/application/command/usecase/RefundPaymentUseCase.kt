package com.restaurant.payment.application.command.usecase

import com.restaurant.payment.application.command.dto.RefundPaymentCommand

/**
 * Use case interface for refunding payment
 */
interface RefundPaymentUseCase {
    /**
     * 결제를 환불합니다.
     */
    suspend fun execute(command: RefundPaymentCommand): String
}
