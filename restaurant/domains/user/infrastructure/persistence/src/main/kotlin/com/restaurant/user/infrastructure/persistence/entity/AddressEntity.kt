package com.restaurant.user.infrastructure.persistence.entity

import com.restaurant.common.infrastructure.persistence.entity.BaseEntity
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID
import java.util.Objects
import jakarta.persistence.Version

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

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AddressEntity
        return Objects.equals(id, that.id) &&
            addressId == that.addressId
    }

    override fun hashCode(): Int {
        return Objects.hash(id ?: addressId)
    }

    override fun toString(): String {
        return "AddressEntity(id=$id, addressId=$addressId, street='$street', detail='$detail', zipCode='$zipCode', isDefault=$isDefault, userId=${user?.id}, version=$version)"
    }
}
