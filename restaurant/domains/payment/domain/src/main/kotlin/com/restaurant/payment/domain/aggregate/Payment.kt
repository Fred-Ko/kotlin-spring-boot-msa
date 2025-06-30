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
 * 결제 정보를 담당하는 AggregateRoot.
 */
class Payment private constructor(
    override val id: PaymentId,
    val orderId: OrderId,
    val userId: UserId,
    val amount: Amount,
    val paymentMethodId: PaymentMethodId,
    val status: PaymentStatus,
    val transactionId: TransactionId?,
    val failureMessage: String?,
    val requestedAt: Instant,
    val completedAt: Instant?,
    override val version: Long = 0L,
) : AggregateRoot<PaymentId>(id) {
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
                    paymentId = payment.id,
                    orderId = payment.orderId,
                    userId = payment.userId,
                    amount = payment.amount,
                    paymentMethodId = payment.paymentMethodId,
                    requestedAt = now,
                ),
            )
            return payment
        }

        fun reconstitute(
            paymentId: PaymentId,
            orderId: OrderId,
            userId: UserId,
            amount: Amount,
            paymentMethodId: PaymentMethodId,
            status: PaymentStatus,
            transactionId: TransactionId?,
            failureMessage: String?,
            requestedAt: Instant,
            completedAt: Instant?,
            version: Long,
        ): Payment =
            Payment(
                id = paymentId,
                orderId = orderId,
                userId = userId,
                amount = amount,
                paymentMethodId = paymentMethodId,
                status = status,
                transactionId = transactionId,
                failureMessage = failureMessage,
                requestedAt = requestedAt,
                completedAt = completedAt,
                version = version,
            )
    }

    fun approve(transactionId: TransactionId): Payment {
        if (this.status != PaymentStatus.PENDING) {
            throw PaymentDomainException.AlreadyProcessed("Payment already processed with status: ${this.status}")
        }
        val now = Instant.now()
        val approvedPayment =
            this.copy(
                status = PaymentStatus.COMPLETED,
                transactionId = transactionId,
                completedAt = now,
            )
        approvedPayment.addDomainEvent(
            PaymentEvent.PaymentApproved(
                paymentId = this.id,
                orderId = this.orderId,
                userId = this.userId,
                transactionId = transactionId,
                approvedAt = now,
            ),
        )
        return approvedPayment
    }

    fun fail(failureMessage: String): Payment {
        if (this.status != PaymentStatus.PENDING) {
            throw PaymentDomainException.AlreadyProcessed("Payment already processed with status: ${this.status}")
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
                paymentId = this.id,
                orderId = this.orderId,
                userId = this.userId,
                failureMessage = failureMessage,
                failedAt = now,
            ),
        )
        return failedPayment
    }

    fun refund(): Payment {
        if (this.status != PaymentStatus.COMPLETED) {
            throw PaymentDomainException.NotRefundable("Payment is not in COMPLETED state.")
        }
        val now = Instant.now()
        val refundedPayment =
            this.copy(
                status = PaymentStatus.REFUNDED,
            )
        refundedPayment.addDomainEvent(
            PaymentEvent.PaymentRefunded(
                paymentId = this.id,
                orderId = this.orderId,
                userId = this.userId,
                refundedAt = now,
            ),
        )
        return refundedPayment
    }

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
        val newPayment =
            Payment(
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
        newPayment.getDomainEvents().addAll(this.getDomainEvents())
        return newPayment
    }
}
