package com.restaurant.payment.infrastructure.repository

import com.restaurant.payment.infrastructure.entity.PaymentMethodEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

/**
 * Spring Data JPA PaymentMethod Repository (Rule 140)
 * Infrastructure 레이어에서 기술별 리포지토리 인터페이스를 정의합니다.
 */
interface SpringDataJpaPaymentMethodRepository : JpaRepository<PaymentMethodEntity, Long> {
    fun findByDomainId(domainId: UUID): PaymentMethodEntity?

    fun findByUserId(userId: UUID): List<PaymentMethodEntity>

    fun findByUserIdAndIsActive(
        userId: UUID,
        isActive: Boolean,
    ): List<PaymentMethodEntity>

    @Query("SELECT pm FROM PaymentMethodEntity pm WHERE pm.userId = :userId AND pm.isDefault = true AND pm.isActive = true")
    fun findDefaultByUserId(
        @Param("userId") userId: UUID,
    ): PaymentMethodEntity?

    @Query(
        "SELECT pm FROM PaymentMethodEntity pm WHERE pm.userId = :userId AND pm.isActive = true ORDER BY pm.isDefault DESC, pm.createdAt DESC",
    )
    fun findActiveByUserIdOrderByDefaultAndCreatedAt(
        @Param("userId") userId: UUID,
    ): List<PaymentMethodEntity>

    fun existsByUserIdAndCardLastFour(
        userId: UUID,
        cardLastFour: String,
    ): Boolean

    fun findByUserIdAndIsDefaultTrue(userId: UUID): PaymentMethodEntity?
}
