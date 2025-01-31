package com.ddd.user.domain.model.aggregate

import com.ddd.user.domain.model.event.UserCreatedEventV1
import com.ddd.user.domain.model.event.UserUpdatedEventV1
import com.ddd.user.domain.model.vo.*
import com.ddd.user.domain.model.vo.UserStatus
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.domain.AbstractAggregateRoot

@ConsistentCopyVisibility
data class User
private constructor(
        val id: UUID,
        var name: UserName,
        var email: Email,
        var phoneNumber: PhoneNumber,
        var address: Address,
        val createdAt: LocalDateTime,
        var updatedAt: LocalDateTime,
        var status: UserStatus,
        var version: Long = 0
) : AbstractAggregateRoot<User>() {

        companion object {
                fun create(
                        name: UserName,
                        email: Email,
                        phoneNumber: PhoneNumber,
                        address: Address
                ): User {
                        return User(
                                        id = UUID.randomUUID(),
                                        name = name,
                                        email = email,
                                        phoneNumber = phoneNumber,
                                        address = address,
                                        createdAt = LocalDateTime.now(),
                                        updatedAt = LocalDateTime.now(),
                                        status = UserStatus.ACTIVE
                                )
                                .apply {
                                        registerEvent(
                                                UserCreatedEventV1(
                                                        aggregateId = id,
                                                        name = name.value,
                                                        email = email.value,
                                                        phoneNumber = phoneNumber.value,
                                                        address = address.toString(),
                                                        status = status.toString()
                                                )
                                        )
                                }
                }
        }

        fun updateProfile(
                name: UserName? = null,
                email: Email? = null,
                phoneNumber: PhoneNumber? = null,
                address: Address? = null
        ) {
                name?.let { this.name = it }
                email?.let { this.email = it }
                phoneNumber?.let { this.phoneNumber = it }
                address?.let { this.address = it }

                this.updatedAt = LocalDateTime.now()
                this.version += 1

                registerEvent(
                        UserUpdatedEventV1(
                                aggregateId = this.id,
                                name = name?.value,
                                email = email?.value,
                                phoneNumber = phoneNumber?.value,
                                address = address?.toString(),
                                status = status.toString()
                        )
                )
        }
}
