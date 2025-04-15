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
import jakarta.persistence.Version
import java.time.LocalDateTime
import java.util.ArrayList

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
    @Version
    @Column(nullable = false)
    val version: Long = 0,
) {
    // JPA 필드는 private으로 설정
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    private var addressEntities: MutableList<AddressEntity> = ArrayList()

    // 주소 목록에 대한 불변 뷰 제공 (프로퍼티로 제공)
    val addresses: List<AddressEntity>
        get() = addressEntities.toList()

    /**
     * 이 메서드는 JPA와 함께 사용하기 위한 메서드이며,
     * 불변성을 유지하면서도 JPA의 양방향 관계를 설정할 수 있게 합니다.
     * 도메인 로직에서 직접 호출하지 않고, 리포지토리 구현체에서만 호출해야 합니다.
     */
    internal fun initializeAddresses(initialAddresses: Collection<AddressEntity>) {
        this.addressEntities.clear()
        initialAddresses.forEach {
            it.user = this
            this.addressEntities.add(it)
        }
    }

    // 주소 추가를 위한 새 UserEntity 생성
    fun withAddress(address: AddressEntity): UserEntity {
        val clone = clone()

        // 주소 설정
        val newAddress = address.copy() // 주소 복제 (AddressEntity에 copy 메서드 추가 필요)
        newAddress.user = clone
        clone.addressEntities.add(newAddress)

        return clone
    }

    // 여러 주소 추가를 위한 새 UserEntity 생성
    fun withAddresses(addressesToAdd: Collection<AddressEntity>): UserEntity {
        val clone = clone()

        // 주소 추가
        addressesToAdd.forEach { address ->
            val newAddress = address.copy() // 주소 복제 (AddressEntity에 copy 메서드 추가 필요)
            newAddress.user = clone
            clone.addressEntities.add(newAddress)
        }

        return clone
    }

    // 모든 주소를 제거한 새 UserEntity 생성
    fun withoutAddresses(): UserEntity {
        val clone = clone()
        clone.addressEntities.clear()
        return clone
    }

    // UserEntity 복제
    private fun clone(): UserEntity {
        val clone =
            UserEntity(
                id = this.id,
                email = this.email,
                password = this.password,
                name = this.name,
                createdAt = this.createdAt,
                updatedAt = LocalDateTime.now(),
                version = this.version,
            )

        // 주소 복사
        this.addressEntities.forEach { address ->
            val newAddress = address.copy() // AddressEntity에 copy 메서드 추가 필요
            newAddress.user = clone
            clone.addressEntities.add(newAddress)
        }

        return clone
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
        "UserEntity(id=$id, email='$email', name='$name', createdAt=$createdAt, updatedAt=$updatedAt, addresses=${addressEntities.size}, version=$version)"
}
