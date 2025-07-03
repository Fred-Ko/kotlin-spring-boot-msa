package com.restaurant.payment.domain.aggregate

import com.restaurant.common.domain.aggregate.AggregateRoot
import com.restaurant.payment.domain.entity.PaymentMethod
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
 * 결제 정보와 결제 수단을 함께 관리하는 AggregateRoot.
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
    private val paymentMethods: MutableList<PaymentMethod> = mutableListOf(), // ← 하위 엔티티 관리
) : AggregateRoot() {
    /**
     * 읽기 전용 결제 수단 목록
     */
    val availablePaymentMethods: List<PaymentMethod>
        get() = paymentMethods.toList()

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

    /**
     * 결제 수단 추가 (애그리거트 내부 관리)
     */
    fun addPaymentMethod(paymentMethod: PaymentMethod): Payment {
        // 동일한 사용자의 결제 수단인지 검증
        if (paymentMethod.userId != this.userId) {
            throw PaymentDomainException.PaymentMethod.IdMismatch(
                this.userId.toString(),
                paymentMethod.userId.toString(),
            )
        }

        // 기본 결제 수단 설정 시 기존 기본 수단 해제
        if (paymentMethod.isDefault) {
            paymentMethods.replaceAll {
                if (it.isDefault) it.unsetAsDefault() else it
            }
        }

        paymentMethods.add(paymentMethod)

        // 도메인 이벤트 발행
        addDomainEvent(
            PaymentEvent.PaymentMethodRegistered(
                id = this.id,
                userId = this.userId.value.toString(),
                paymentMethodId = paymentMethod.paymentMethodId,
                paymentMethodType = paymentMethod.type.name,
                alias = paymentMethod.alias,
                isDefault = paymentMethod.isDefault,
                occurredAt = Instant.now(),
            ),
        )

        return this
    }

    /**
     * 결제 수단 제거
     */
    fun removePaymentMethod(paymentMethodId: PaymentMethodId): Payment {
        val paymentMethod =
            paymentMethods.find { it.paymentMethodId == paymentMethodId }
                ?: throw PaymentDomainException.PaymentMethod.NotFound(paymentMethodId.toString())

        // 마지막 결제 수단 삭제 방지
        if (paymentMethods.size == 1) {
            throw PaymentDomainException.PaymentMethod.CannotDeleteLast()
        }

        // 기본 결제 수단 삭제 시 다른 수단을 기본으로 설정
        if (paymentMethod.isDefault && paymentMethods.size > 1) {
            val nextDefault = paymentMethods.find { it.paymentMethodId != paymentMethodId }!!
            val index = paymentMethods.indexOf(nextDefault)
            paymentMethods[index] = nextDefault.setAsDefault()
        }

        paymentMethods.removeIf { it.paymentMethodId == paymentMethodId }
        return this
    }

    /**
     * 특정 결제 수단 조회
     */
    fun getPaymentMethod(paymentMethodId: PaymentMethodId): PaymentMethod? = paymentMethods.find { it.paymentMethodId == paymentMethodId }

    /**
     * 기본 결제 수단 조회
     */
    fun getDefaultPaymentMethod(): PaymentMethod? = paymentMethods.find { it.isDefault }

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
        paymentMethods: MutableList<PaymentMethod> = this.paymentMethods,
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
                paymentMethods,
            )
        // 기존 도메인 이벤트들을 새 인스턴스에 복사
        this.getDomainEvents().forEach { event ->
            newPayment.addDomainEvent(event)
        }
        return newPayment
    }
}
