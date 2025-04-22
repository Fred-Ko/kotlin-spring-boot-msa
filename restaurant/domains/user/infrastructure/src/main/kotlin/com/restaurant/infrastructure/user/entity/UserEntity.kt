package com.restaurant.infrastructure.user.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Column(name = "domain_id", unique = true, nullable = false, updatable = false)
    val domainId: UUID,
    @Column(unique = true, nullable = false) val email: String,
    @Column(nullable = false) val password: String,
    @Column(nullable = false) val name: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    @Version
    @Column(nullable = false)
    val version: Long = 0,
) {
    // JPA 필드는 private으로 설정
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private var addressEntities: MutableList<AddressEntity> = mutableListOf()

    // 주소 목록에 대한 불변 뷰 제공 (프로퍼티로 제공)
    val addresses: List<AddressEntity>
        get() = addressEntities.toList()

    fun addAddress(address: AddressEntity) {
        this.addressEntities.add(address)
    }

    fun removeAddress(address: AddressEntity) {
        this.addressEntities.remove(address)
    }

    fun setAddresses(addresses: List<AddressEntity>) {
        this.addressEntities.clear()
        this.addressEntities.addAll(addresses)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        if (id != other.id) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + email.hashCode()
        return result
    }

    override fun toString(): String =
        "UserEntity(id=$id, domainId=$domainId, email='$email', name='$name', createdAt=$createdAt, updatedAt=$updatedAt, addresses=${addressEntities.size}, version=$version)"
}
