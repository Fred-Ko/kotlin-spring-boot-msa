package com.restaurant.payment.application.command.handler

import com.restaurant.payment.application.command.dto.RegisterBankTransferCommand
import com.restaurant.payment.application.command.dto.RegisterCreditCardCommand
import com.restaurant.payment.application.command.dto.RegisterPaymentMethodCommand
import com.restaurant.payment.application.command.usecase.RegisterPaymentMethodUseCase
import com.restaurant.payment.domain.entity.BankTransfer
import com.restaurant.payment.domain.entity.CreditCard
import com.restaurant.payment.domain.repository.PaymentMethodRepository
import com.restaurant.payment.domain.vo.PaymentMethodId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterPaymentMethodUseCaseHandler(
    private val paymentMethodRepository: PaymentMethodRepository,
) : RegisterPaymentMethodUseCase {
    @Transactional
    override suspend fun execute(command: RegisterPaymentMethodCommand): String {
        if (command.isDefault) {
            paymentMethodRepository.findByUserIdAndIsDefault(command.userId)?.let {
                val nonDefaultMethod = it.unsetAsDefault()
                paymentMethodRepository.save(nonDefaultMethod)
            }
        }

        val newPaymentMethod =
            when (command) {
                is RegisterCreditCardCommand ->
                    CreditCard.create(
                        paymentMethodId = PaymentMethodId.generate(),
                        userId = command.userId,
                        alias = command.alias,
                        cardNumber = command.cardNumber,
                        cardExpiry = command.cardExpiry,
                        cardCvv = command.cardCvv,
                        isDefault = command.isDefault,
                    )
                is RegisterBankTransferCommand ->
                    BankTransfer.create(
                        paymentMethodId = PaymentMethodId.generate(),
                        userId = command.userId,
                        alias = command.alias,
                        bankName = command.bankName,
                        accountNumber = command.accountNumber,
                        isDefault = command.isDefault,
                    )
            }

        val savedMethod = paymentMethodRepository.save(newPaymentMethod)
        return savedMethod.paymentMethodId.toString()
    }
}
