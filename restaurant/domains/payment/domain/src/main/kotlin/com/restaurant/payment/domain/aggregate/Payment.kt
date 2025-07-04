package com.restaurant.payment.domain.aggregate

import com.restaurant.common.domain.aggregate.AggregateRoot
import com.restaurant.payment.domain.event.PaymentEvent
import com.restaurant.payment.domain.exception.PaymentDomainException
import com.restaurant.payment.domain.vo.Amount
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.PaymentStatus
import com.restaurant.payment.domain.vo.TransactionId
import com.restaurant.payment.domain.vo.UserId
import java.time.Instant

/**
 * Payment AggregateRoot (Rule 11, 17)
 * 결제 트랜잭션 정보를 관리하는 AggregateRoot.
 * Rule 11에 따라 불변 객체로 설계됩니다.
 */
class Payment internal constructor(
    val id: PaymentId,
    val orderId: OrderId,
    val userId: UserId,
    val amount: Amount,
    val paymentMethodId: PaymentMethodId,
    val status: PaymentStatus,
    val transactionId: TransactionId?,
    val failureMessage: String?,
    val requestedAt: Instant,
    val completedAt: Instant?,
    val version: Long = 0L,
) : AggregateRoot() {
    companion object {
        fun create(
            paymentId: PaymentId,
            orderId: OrderId,
            userId: UserId,
            amount: Amount,
            paymentMethodId: PaymentMethodId,
        ): Payment {
            val now = Instant.now()
            val payment =
                Payment(
                    id = paymentId,
                    orderId = orderId,
                    userId = userId,
                    amount = amount,
                    paymentMethodId = paymentMethodId,
                    status = PaymentStatus.PENDING,
                    transactionId = null,
                    failureMessage = null,
                    requestedAt = now,
                    completedAt = null,
                    version = 0L,
                )
            payment.addDomainEvent(
                PaymentEvent.PaymentRequested(
                    id = payment.id,
                    orderId = payment.orderId.value.toString(),
                    userId = payment.userId.value.toString(),
                    amount = payment.amount.value,
                    paymentMethodId = payment.paymentMethodId.value.toString(),
                    occurredAt = now,
                ),
            )
            return payment
        }
    }

    fun approve(transactionId: TransactionId): Payment {
        if (this.status != PaymentStatus.PENDING) {
            throw PaymentDomainException.Payment.AlreadyApproved(this.id.value.toString())
        }
        val now = Instant.now()
        val approvedPayment =
            this.copy(
                status = PaymentStatus.APPROVED,
                transactionId = transactionId,
                completedAt = now,
            )
        approvedPayment.addDomainEvent(
            PaymentEvent.PaymentApproved(
                id = this.id,
                orderId = this.orderId.value.toString(),
                userId = this.userId.value.toString(),
                transactionId = transactionId.value.toString(),
                amount = this.amount.value,
                paymentMethodId = this.paymentMethodId.value.toString(),
                occurredAt = now,
            ),
        )
        return approvedPayment
    }

    fun fail(failureMessage: String): Payment {
        if (this.status != PaymentStatus.PENDING) {
            throw PaymentDomainException.Payment.AlreadyFailed(this.id.value.toString())
        }
        val now = Instant.now()
        val failedPayment =
            this.copy(
                status = PaymentStatus.FAILED,
                failureMessage = failureMessage,
                completedAt = now,
            )
        failedPayment.addDomainEvent(
            PaymentEvent.PaymentFailed(
                id = this.id,
                orderId = this.orderId.value.toString(),
                userId = this.userId.value.toString(),
                amount = this.amount.value,
                paymentMethodId = this.paymentMethodId.value.toString(),
                failureReason = failureMessage,
                occurredAt = now,
            ),
        )
        return failedPayment
    }

    fun refund(): Payment {
        if (this.status != PaymentStatus.APPROVED) {
            throw PaymentDomainException.Payment.CannotBeRefunded(this.id.value.toString())
        }
        val now = Instant.now()
        val refundedPayment =
            this.copy(
                status = PaymentStatus.REFUNDED,
            )
        refundedPayment.addDomainEvent(
            PaymentEvent.PaymentRefunded(
                id = this.id,
                orderId = this.orderId.value.toString(),
                userId = this.userId.value.toString(),
                originalAmount = this.amount.value,
                refundedAmount = this.amount.value,
                reason = null,
                occurredAt = now,
            ),
        )
        return refundedPayment
    }

    /**
     * Rule 18에 따라 도메인 이벤트 중복 방지를 위한 copy 메서드
     */
    private fun copy(
        id: PaymentId = this.id,
        orderId: OrderId = this.orderId,
        userId: UserId = this.userId,
        amount: Amount = this.amount,
        paymentMethodId: PaymentMethodId = this.paymentMethodId,
        status: PaymentStatus = this.status,
        transactionId: TransactionId? = this.transactionId,
        failureMessage: String? = this.failureMessage,
        requestedAt: Instant = this.requestedAt,
        completedAt: Instant? = this.completedAt,
        version: Long = this.version,
    ): Payment {
        // Rule 18에 따라 기존 도메인 이벤트는 복사하지 않음 (중복 방지)
        return Payment(
            id,
            orderId,
            userId,
            amount,
            paymentMethodId,
            status,
            transactionId,
            failureMessage,
            requestedAt,
            completedAt,
            version,
        )
    }
}
