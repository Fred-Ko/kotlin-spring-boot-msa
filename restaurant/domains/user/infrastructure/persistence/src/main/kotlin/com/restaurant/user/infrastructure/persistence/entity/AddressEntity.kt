package com.restaurant.user.infrastructure.persistence.entity

import com.restaurant.common.infrastructure.persistence.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "addresses")
class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "address_id", unique = true, nullable = false, updatable = false)
    val addressId: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    var street: String,
    @Column(nullable = false)
    var detail: String,
    @Column(nullable = false, length = 10)
    var zipCode: String,
    @Column(nullable = false)
    var isDefault: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null,
    @Version
    @Column(nullable = false)
    val version: Long = 0L,
) : BaseEntity() {

}
