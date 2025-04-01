package com.restaurant.infrastructure.user.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Column(unique = true, nullable = false) val email: String,
    @Column(nullable = false) val password: String,
    @Column(nullable = false) val name: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    private val _addresses: MutableList<AddressEntity> = mutableListOf()

    // 주소 목록에 대한 불변 뷰 제공
    val addresses: List<AddressEntity>
        get() = _addresses.toList()

    // 주소 추가 메서드
    fun addAddress(address: AddressEntity) {
        address.user = this
        _addresses.add(address)
    }

    // 여러 주소 추가 메서드
    fun addAddresses(addressesToAdd: Collection<AddressEntity>) {
        addressesToAdd.forEach { addAddress(it) }
    }

    // 모든 주소 제거 메서드
    fun clearAddresses() {
        _addresses.clear()
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
        "UserEntity(id=$id, email='$email', name='$name', createdAt=$createdAt, updatedAt=$updatedAt, addresses=${addresses.size})"
}
