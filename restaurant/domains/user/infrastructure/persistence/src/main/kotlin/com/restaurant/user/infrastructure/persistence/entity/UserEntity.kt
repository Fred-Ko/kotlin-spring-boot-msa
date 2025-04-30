package com.restaurant.user.infrastructure.persistence.entity

import com.restaurant.user.domain.aggregate.UserStatus
import com.restaurant.user.domain.aggregate.UserType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.CascadeType
import java.time.Instant
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "domain_id", unique = true, nullable = false, updatable = false)
    val domainId: UUID,
    @Column(unique = true, nullable = false, length = 50)
    val username: String,
    @Column(nullable = false)
    val passwordHash: String,
    @Column(unique = true, nullable = false)
    val email: String,
    @Column(nullable = false, length = 50)
    val name: String,
    @Column(length = 20)
    val phoneNumber: String?,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val userType: UserType,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: UserStatus = UserStatus.ACTIVE,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "user")
    val addresses: List<AddressEntity> = listOf(),
    @Version
    @Column(nullable = false)
    val version: Long = 0L,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as UserEntity
        return Objects.equals(id, that.id) &&
            domainId == that.domainId
    }

    override fun hashCode(): Int {
        return Objects.hash(id ?: domainId)
    }

    override fun toString(): String =
        "UserEntity(id=$id, domainId=$domainId, username='$username', email='$email', userType=$userType, createdAt=$createdAt, updatedAt=$updatedAt, addressId = AddressId.of(address.addressId), addresses=${addresses.size}, version=$version)"
}
