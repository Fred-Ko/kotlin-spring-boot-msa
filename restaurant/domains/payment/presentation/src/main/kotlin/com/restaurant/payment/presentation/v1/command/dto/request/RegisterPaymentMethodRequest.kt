package com.restaurant.payment.presentation.v1.command.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RegisterCreditCardRequest::class, name = "CREDIT_CARD"),
    JsonSubTypes.Type(value = RegisterBankTransferRequest::class, name = "BANK_TRANSFER"),
)
sealed interface RegisterPaymentMethodRequest {
    val alias: String
    val isDefault: Boolean
}

data class RegisterCreditCardRequest(
    @field:NotBlank
    @field:Size(min = 2, max = 50)
    override val alias: String,
    override val isDefault: Boolean,
    @field:NotBlank
    val cardNumber: String,
    @field:NotBlank
    val cardExpiry: String,
    @field:NotBlank
    val cardCvv: String,
) : RegisterPaymentMethodRequest

data class RegisterBankTransferRequest(
    @field:NotBlank
    @field:Size(min = 2, max = 50)
    override val alias: String,
    override val isDefault: Boolean,
    @field:NotBlank
    val bankName: String,
    @field:NotBlank
    val accountNumber: String,
) : RegisterPaymentMethodRequest
