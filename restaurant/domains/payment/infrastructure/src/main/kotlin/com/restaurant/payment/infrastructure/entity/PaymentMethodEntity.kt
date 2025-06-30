package com.restaurant.payment.infrastructure.entity

import com.restaurant.common.infrastructure.entity.BaseEntity
import com.restaurant.payment.domain.vo.PaymentMethodType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import java.util.UUID

/**
 * PaymentMethod JPA Entity (Rule 19-31)
 * Infrastructure 레이어에서 영속화 구조를 반영하며, 도메인 로직을 포함하지 않습니다.
 */
@Entity
@Table(name = "payment_methods")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
abstract class PaymentMethodEntity(
    @Column(nullable = false, unique = true)
    val domainId: UUID,
    @Column(nullable = false)
    val userId: UUID,
    @Column(name = "type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    val type: PaymentMethodType,
    @Column(nullable = false)
    val alias: String,
    @Column(nullable = false)
    val isDefault: Boolean,
) : BaseEntity()
