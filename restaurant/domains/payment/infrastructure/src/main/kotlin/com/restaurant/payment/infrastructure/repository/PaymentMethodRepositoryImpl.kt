package com.restaurant.payment.infrastructure.repository

import com.restaurant.payment.domain.aggregate.PaymentMethod
import com.restaurant.payment.domain.repository.PaymentMethodRepository
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.infrastructure.mapper.toDomain
import com.restaurant.payment.infrastructure.mapper.toEntity
import org.springframework.stereotype.Repository

/**
 * PaymentMethodRepository 구현체 (Rule 138, 139)
 * Infrastructure 레이어에서 Domain Repository 인터페이스를 구현합니다.
 */
@Repository
class PaymentMethodRepositoryImpl(
    private val jpaRepository: SpringDataJpaPaymentMethodRepository,
) : PaymentMethodRepository {
    override fun save(paymentMethod: PaymentMethod): PaymentMethod {
        val entity = paymentMethod.toEntity()
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(paymentMethodId: PaymentMethodId): PaymentMethod? {
        val entity = jpaRepository.findByDomainId(paymentMethodId.value) ?: return null
        return entity.toDomain()
    }

    override fun findByUserId(userId: UserId): List<PaymentMethod> {
        val entities = jpaRepository.findByUserId(userId.value)
        return entities.map { it.toDomain() }
    }

    override fun findDefaultByUserId(userId: UserId): PaymentMethod? {
        val entity = jpaRepository.findByUserIdAndIsDefaultTrue(userId.value) ?: return null
        return entity.toDomain()
    }

    override fun delete(paymentMethod: PaymentMethod) {
        jpaRepository.deleteByDomainId(paymentMethod.paymentMethodId.value)
    }

    override fun deleteById(paymentMethodId: PaymentMethodId) {
        jpaRepository.deleteByDomainId(paymentMethodId.value)
    }

    override fun existsById(paymentMethodId: PaymentMethodId): Boolean = jpaRepository.existsByDomainId(paymentMethodId.value)
}
