package com.restaurant.infrastructure.user.entity

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

@Entity
@Table(name = "user_addresses")
class AddressEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Column(nullable = false) val street: String,
    @Column val detail: String,
    @Column(name = "zip_code", nullable = false) val zipCode: String,
    @Column(name = "is_default", nullable = false) val isDefault: Boolean,
    @Version
    @Column(nullable = false)
    val version: Long = 0,
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: UserEntity

    // 양방향 참조를 위한 생성자
    constructor(
        id: Long? = null,
        user: UserEntity,
        street: String,
        detail: String,
        zipCode: String,
        isDefault: Boolean,
    ) : this(id, street, detail, zipCode, isDefault) {
        this.user = user
    }

    /**
     * 엔티티의 복사본을 생성합니다.
     * user 속성은 복사되지 않으므로 별도로 설정해주어야 합니다.
     */
    fun copy(
        id: Long? = this.id,
        street: String = this.street,
        detail: String = this.detail,
        zipCode: String = this.zipCode,
        isDefault: Boolean = this.isDefault,
        version: Long = this.version,
    ): AddressEntity =
        AddressEntity(
            id = id,
            street = street,
            detail = detail,
            zipCode = zipCode,
            isDefault = isDefault,
            version = version,
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddressEntity

        if (id != null && other.id != null) {
            return id == other.id
        }

        if (street != other.street) return false
        if (detail != other.detail) return false
        if (zipCode != other.zipCode) return false

        return true
    }

    override fun hashCode(): Int = id?.hashCode() ?: (street.hashCode() + zipCode.hashCode())

    override fun toString(): String =
        "AddressEntity(id=$id, street='$street', detail='$detail', zipCode='$zipCode', isDefault=$isDefault, version=$version)"
}
