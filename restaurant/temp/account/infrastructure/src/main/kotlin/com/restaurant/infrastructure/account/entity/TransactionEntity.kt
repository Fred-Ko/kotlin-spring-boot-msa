package com.restaurant.infrastructure.account.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.time.Instant

/**
 * 거래 내역 JPA 엔티티
 */
@Entity
@Table(name = "account_transactions")
class TransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "account_id", nullable = false)
    val accountId: Long,
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: AccountTransactionTypeEntity,
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    val amount: BigDecimal,
    @Column(name = "order_id", nullable = false)
    val orderId: String,
    @Column(name = "cancelled", nullable = false)
    val cancelled: Boolean = false,
    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP")
    val timestamp: Instant,
    @Version
    @Column(nullable = false)
    val version: Long = 0,
)

/**
 * 거래 유형 Enum
 */
enum class AccountTransactionTypeEntity {
    DEBIT, // 출금 (계좌에서 차감)
    CREDIT, // 입금 (계좌로 추가)
}
