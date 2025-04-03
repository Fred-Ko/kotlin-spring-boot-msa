package com.restaurant.infrastructure.account.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

/**
 * 계좌 JPA 엔티티
 */
@Entity
@Table(name = "accounts")
class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(name = "balance", nullable = false)
    val balance: BigDecimal,
)
