package com.restaurant.payment.infrastructure.repository

import com.restaurant.payment.domain.entity.PaymentMethod
import com.restaurant.payment.domain.repository.PaymentMethodRepository
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.UserId
import com.restaurant.payment.infrastructure.mapper.toDomain
import com.restaurant.payment.infrastructure.mapper.toEntity
import org.springframework.stereotype.Repository

@Repository
class PaymentMethodRepositoryImpl(
    private val jpaRepository: SpringDataJpaPaymentMethodRepository,
) : PaymentMethodRepository {
    override fun save(paymentMethod: PaymentMethod): PaymentMethod {
        val entity = paymentMethod.toEntity()
        val savedEntity = jpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(paymentMethodId: PaymentMethodId): PaymentMethod? =
        jpaRepository.findByDomainId(paymentMethodId.value)?.toDomain()

    override fun findByUserId(userId: UserId): List<PaymentMethod> = jpaRepository.findByUserId(userId.value).map { it.toDomain() }

    override fun findByUserIdAndIsDefault(userId: UserId): PaymentMethod? =
        jpaRepository.findByUserIdAndIsDefaultTrue(userId.value)?.toDomain()
}
