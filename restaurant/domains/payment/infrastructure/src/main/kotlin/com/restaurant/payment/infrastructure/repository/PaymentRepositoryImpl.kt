package com.restaurant.payment.infrastructure.repository

import com.restaurant.outbox.application.dto.OutboxMessageRepository
import com.restaurant.payment.domain.aggregate.Payment
import com.restaurant.payment.domain.event.PaymentEvent
import com.restaurant.payment.domain.repository.PaymentRepository
import com.restaurant.payment.domain.vo.OrderId
import com.restaurant.payment.domain.vo.PaymentId
import com.restaurant.payment.domain.vo.PaymentStatus
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.infrastructure.mapper.DomainEventToOutboxMessageConverter
import com.restaurant.payment.infrastructure.mapper.toDomain
import com.restaurant.payment.infrastructure.mapper.toEntity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * PaymentRepository 구현체 (Rule 138, 139)
 * Infrastructure 레이어에서 Domain Repository 인터페이스를 구현합니다.
 */
@Repository
class PaymentRepositoryImpl(
    private val jpaRepository: SpringDataJpaPaymentRepository,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val domainEventConverter: DomainEventToOutboxMessageConverter,
) : PaymentRepository {
    @Transactional
    override fun save(payment: Payment): Payment {
        val entity = payment.toEntity()
        val savedEntity = jpaRepository.save(entity)

        // 도메인 이벤트를 Outbox 메시지로 변환하여 저장
        val domainEvents = payment.getDomainEvents()
        domainEvents.forEach { event ->
            val outboxMessage = domainEventConverter.convert(event as PaymentEvent)
            outboxMessageRepository.save(outboxMessage)
        }

        // Payment 애그리거트 반환 (PaymentMethod 컬렉션 없이)
        val savedPayment = savedEntity.toDomain()

        // 도메인 이벤트 초기화
        savedPayment.clearDomainEvents()

        return savedPayment
    }

    override fun findById(paymentId: PaymentId): Payment? {
        val entity = jpaRepository.findByDomainId(paymentId.value) ?: return null
        return entity.toDomain()
    }

    override fun findByOrderId(orderId: OrderId): Payment? {
        val entity = jpaRepository.findByOrderId(orderId.value) ?: return null
        return entity.toDomain()
    }

    override fun findByUserId(userId: UserId): List<Payment> {
        val entities = jpaRepository.findByUserId(userId.value)
        return entities.map { entity -> entity.toDomain() }
    }

    override fun findByStatus(status: PaymentStatus): List<Payment> {
        val entities = jpaRepository.findByStatus(status.name)
        return entities.map { entity -> entity.toDomain() }
    }

    override fun findByUserIdAndStatus(
        userId: UserId,
        status: PaymentStatus,
    ): List<Payment> {
        val entities = jpaRepository.findByUserIdAndStatus(userId.value, status.name)
        return entities.map { entity -> entity.toDomain() }
    }

    override fun delete(payment: Payment) {
        jpaRepository.findByDomainId(payment.id.value)?.let { entity ->
            jpaRepository.delete(entity)
        }
    }

    override fun deleteById(paymentId: PaymentId) {
        jpaRepository.findByDomainId(paymentId.value)?.let { entity ->
            jpaRepository.delete(entity)
        }
    }
}
