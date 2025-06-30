package com.restaurant.payment.infrastructure.repository

import com.restaurant.outbox.application.dto.OutboxMessageRepository
import com.restaurant.payment.domain.aggregate.Payment
import com.restaurant.payment.domain.event.PaymentEvent
import com.restaurant.payment.domain.repository.PaymentRepository
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.infrastructure.mapper.DomainEventToOutboxMessageConverter
import com.restaurant.payment.infrastructure.mapper.toDomain
import com.restaurant.payment.infrastructure.mapper.toEntity
import org.springframework.stereotype.Repository

/**
 * PaymentRepository 구현체 (Rule 138, 139)
 * Infrastructure 레이어에서 Domain Repository 인터페이스를 구현합니다.
 */
@Repository
class PaymentRepositoryImpl(
    private val springDataJpaPaymentRepository: SpringDataJpaPaymentRepository,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val domainEventToOutboxMessageConverter: DomainEventToOutboxMessageConverter,
) : PaymentRepository {
    override suspend fun findById(paymentId: PaymentId): Payment? =
        springDataJpaPaymentRepository.findByDomainId(paymentId.value)?.toDomain()

    override suspend fun findByOrderId(orderId: OrderId): Payment? = springDataJpaPaymentRepository.findByOrderId(orderId.value)?.toDomain()

    override suspend fun findByUserId(userId: UserId): List<Payment> =
        springDataJpaPaymentRepository
            .findByUserIdOrderByCreatedAtDesc(userId.value)
            .map { it.toDomain() }

    override suspend fun findByUserIdWithPagination(
        userId: UserId,
        offset: Int,
        limit: Int,
    ): List<Payment> =
        springDataJpaPaymentRepository
            .findByUserIdOrderByCreatedAtDesc(userId.value)
            .drop(offset)
            .take(limit)
            .map { it.toDomain() }

    override suspend fun existsById(paymentId: PaymentId): Boolean = springDataJpaPaymentRepository.existsByDomainId(paymentId.value)

    override suspend fun existsByOrderId(orderId: OrderId): Boolean = springDataJpaPaymentRepository.existsByOrderId(orderId.value)

    override suspend fun save(payment: Payment): Payment {
        val paymentEntity = payment.toEntity()

        // 양방향 관계 설정 (PaymentMethodEntity들이 PaymentEntity를 참조하도록)
        paymentEntity.paymentMethods.forEach { it.payment = paymentEntity }

        val savedEntity = springDataJpaPaymentRepository.save(paymentEntity)

        // 도메인 이벤트 처리 (Rule 85)
        val domainEvents = payment.getDomainEvents()
        if (domainEvents.isNotEmpty()) {
            val outboxMessages =
                domainEvents.map {
                    domainEventToOutboxMessageConverter.convert(it as PaymentEvent)
                }

            if (outboxMessages.isNotEmpty()) {
                outboxMessageRepository.saveAll(outboxMessages)
            }

            payment.clearDomainEvents()
        }

        return savedEntity.toDomain()
    }

    override suspend fun delete(paymentId: PaymentId) {
        val payment = findById(paymentId)
        if (payment != null) {
            springDataJpaPaymentRepository.delete(payment.toEntity())
        }
    }
}
