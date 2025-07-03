package com.restaurant.payment.infrastructure.mapper

import com.restaurant.payment.domain.aggregate.Payment
import com.restaurant.payment.domain.vo.Amount
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.TransactionId
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.infrastructure.entity.PaymentEntity

/**
 * PaymentEntity와 Payment Domain 객체 간 변환 (Rule 24-25)
 */
fun PaymentEntity.toDomain(): Payment =
    Payment(
        id = PaymentId.of(this.domainId),
        orderId = OrderId.of(this.orderId),
        userId = UserId.of(this.userId),
        amount = Amount.of(this.amount),
        paymentMethodId = PaymentMethodId.of(this.paymentMethodId),
        status = this.status,
        transactionId = this.transactionId?.let { TransactionId.of(it) },
        failureMessage = this.failureMessage,
        requestedAt = this.requestedAt,
        completedAt = this.completedAt,
        version = 0L, // 기본값 사용
    )

fun Payment.toEntity(): PaymentEntity =
    PaymentEntity(
        domainId = this.id.value,
        orderId = this.orderId.value,
        userId = this.userId.value,
        amount = this.amount.value,
        paymentMethodId = this.paymentMethodId.value,
        status = this.status,
        transactionId = this.transactionId?.value,
        failureMessage = this.failureMessage,
        requestedAt = this.requestedAt,
        completedAt = this.completedAt,
    )
