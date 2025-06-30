package com.restaurant.payment.infrastructure.repository

import com.restaurant.payment.infrastructure.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

/**
 * Spring Data JPA Payment Repository (Rule 140)
 * Infrastructure 레이어에서 기술별 리포지토리 인터페이스를 정의합니다.
 */
interface SpringDataJpaPaymentRepository : JpaRepository<PaymentEntity, Long> {
    fun findByDomainId(domainId: UUID): PaymentEntity?

    fun findByOrderId(orderId: UUID): PaymentEntity?

    fun findByUserId(userId: UUID): List<PaymentEntity>

    fun findByUserIdAndOrderId(
        userId: UUID,
        orderId: UUID,
    ): PaymentEntity?

    fun findByTransactionId(transactionId: String): PaymentEntity?

    @Query("SELECT p FROM PaymentEntity p WHERE p.userId = :userId ORDER BY p.createdAt DESC")
    fun findByUserIdOrderByCreatedAtDesc(
        @Param("userId") userId: UUID,
    ): List<PaymentEntity>

    fun existsByDomainId(domainId: UUID): Boolean

    fun existsByOrderId(orderId: UUID): Boolean

    fun existsByTransactionId(transactionId: String): Boolean
}
