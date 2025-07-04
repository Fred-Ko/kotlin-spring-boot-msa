package com.restaurant.payment.application.command.usecase

import com.restaurant.payment.application.command.dto.ProcessPaymentCommand

/**
 * Use case interface for processing payment
 */
interface ProcessPaymentUseCase {
    /**
     * 결제를 처리합니다.
     */
    fun execute(command: ProcessPaymentCommand): String
}
