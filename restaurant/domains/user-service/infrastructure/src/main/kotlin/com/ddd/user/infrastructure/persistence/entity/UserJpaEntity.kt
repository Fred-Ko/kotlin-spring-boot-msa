package com.ddd.user.infrastructure.persistence.entity

import com.ddd.support.entity.BaseJpaEntity
import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.model.vo.*
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
        @Id @Column(nullable = false, columnDefinition = "UUID") override var id: UUID? = null,
        @Column(nullable = false) var name: String,
        @Column(nullable = false, unique = true) var email: String,
        @Column(name = "phone_number", nullable = false) var phoneNumber: String,
        @Column(name = "password", nullable = false) var password: String,
        @Embedded var address: AddressEmbeddable,
        @Enumerated(EnumType.STRING) @Column(nullable = false) var status: String,
        @Version override var version: Long = 0
) : BaseJpaEntity<UUID>() {

    fun toDomain(): User {
        return User.create(
                id = id!!,
                name = UserName.of(name),
                email = Email.of(email),
                phoneNumber = PhoneNumber.of(phoneNumber),
                address = address.toDomain(),
                password = Password.of(password),
                createdAt = createdAt,
                updatedAt = updatedAt,
                status = UserStatus.valueOf(status),
                version = version
        )
    }

    companion object {
        fun from(user: User): UserJpaEntity {
            return UserJpaEntity(
                    id = user.id,
                    name = user.name.value,
                    email = user.email.value,
                    phoneNumber = user.phoneNumber.value,
                    password = user.password.value,
                    address = AddressEmbeddable.from(user.address),
                    status = user.status.toString(),
                    version = user.version
            )
        }
    }
}

@Embeddable
class AddressEmbeddable(
        @Column(name = "street", nullable = false) var street: String,
        @Column(name = "city", nullable = false) var city: String,
        @Column(name = "state", nullable = false) var state: String,
        @Column(name = "zip_code", nullable = false) var zipCode: String
) {
    fun toDomain() = Address.of(street, city, state, zipCode)

    companion object {
        fun from(address: Address) =
                AddressEmbeddable(
                        street = address.street,
                        city = address.city,
                        state = address.state,
                        zipCode = address.zipCode
                )
    }
}
