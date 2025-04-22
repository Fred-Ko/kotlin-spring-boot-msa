package com.restaurant.infrastructure.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.util.UUID

@Entity
@Table(name = "user_addresses")
class AddressEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Column(name = "address_id", nullable = false, unique = true) val addressId: UUID,
    @Column(nullable = false) val street: String,
    @Column val detail: String,
    @Column(name = "zip_code", nullable = false) val zipCode: String,
    @Column(name = "is_default", nullable = false) val isDefault: Boolean,
    @Version
    @Column(nullable = false)
    val version: Long = 0,
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "user_id", nullable = false)
    // var user: UserEntity? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddressEntity

        // 도메인 ID로 비교
        return addressId == other.addressId
    }

    override fun hashCode(): Int {
        // 도메인 ID 기반 해시코드
        return addressId.hashCode()
    }

    override fun toString(): String =
        "AddressEntity(id=$id, addressId=$addressId, street='$street', detail='$detail', zipCode='$zipCode', isDefault=$isDefault, version=$version)"
}
