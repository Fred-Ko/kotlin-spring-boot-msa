package com.ddd.user.infrastructure.persistence.entity

import com.ddd.support.infrastructure.entity.BaseEntity
import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.model.vo.*
import jakarta.persistence.*
import java.util.UUID
import org.hibernate.annotations.UuidGenerator

@Entity
@Table(name = "users")
class UserEntity(
        @Id
        @GeneratedValue
        @UuidGenerator
        @Column(columnDefinition = "BINARY(16)")
        override var id: UUID? = null,
        @Column(nullable = false) var name: String,
        @Column(nullable = false, unique = true) var email: String,
        @Column(name = "phone_number", nullable = false) var phoneNumber: String,
        @Embedded var address: AddressEmbeddable,
) : BaseEntity<UUID>() {

    fun toDomain(): User {
        return User.create(
                name = UserName.of(name),
                email = Email.of(email),
                phoneNumber = PhoneNumber.of(phoneNumber),
                address = address.toDomain()
        )
    }

    companion object {
        fun from(user: User): UserEntity {
            return UserEntity(
                    id = user.id,
                    name = user.name.value,
                    email = user.email.value,
                    phoneNumber = user.phoneNumber.value,
                    address = AddressEmbeddable.from(user.address)
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
