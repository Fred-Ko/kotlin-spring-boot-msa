package com.restaurant.account.infrastructure.entity

import com.restaurant.account.domain.aggregate.AccountStatus
import com.restaurant.common.infrastructure.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "accounts", indexes = [Index(name = "idx_user_id", columnList = "user_id")])
class AccountEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "domain_id", unique = true, nullable = false, updatable = false)
    val domainId: UUID,
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: UUID,
    @Column(nullable = false, precision = 19, scale = 4)
    val balance: BigDecimal,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: AccountStatus,
    @Version
    @Column(nullable = false)
    val version: Long = 0L,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AccountEntity
        return Objects.equals(id, that.id) && domainId == that.domainId
    }

    override fun hashCode(): Int = Objects.hash(id ?: domainId)

    override fun toString(): String =
        "AccountEntity(id=$id, domainId=$domainId, userId=$userId, balance=$balance, status=$status, version=$version)"
}
