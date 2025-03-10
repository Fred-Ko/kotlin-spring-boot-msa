package com.ddd.user.domain.model.aggregate

import com.ddd.support.aggregate.AbstractAggregateRoot
import com.ddd.user.domain.model.vo.*
import com.ddd.user.domain.model.vo.UserStatus
import java.time.LocalDateTime
import java.util.UUID

@ConsistentCopyVisibility
data class User
private constructor(
        val id: UUID,
        val createdAt: LocalDateTime,
        val name: UserName,
        val email: Email,
        val password: Password,
        val phoneNumber: PhoneNumber,
        val address: Address,
        val updatedAt: LocalDateTime,
        val status: UserStatus,
        val version: Long = 0
) : AbstractAggregateRoot<User>() {

        companion object {
                fun create(
                        name: UserName,
                        email: Email,
                        password: Password,
                        phoneNumber: PhoneNumber,
                        address: Address
                ): User {
                        return User(
                                id = UUID.randomUUID(),
                                name = name,
                                email = email,
                                password = password,
                                phoneNumber = phoneNumber,
                                address = address,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now(),
                                status = UserStatus.ACTIVE,
                                version = 0
                        )
                }

                fun fromEntity(
                        id: UUID,
                        createdAt: LocalDateTime,
                        name: UserName,
                        email: Email,
                        password: Password,
                        phoneNumber: PhoneNumber,
                        address: Address,
                        updatedAt: LocalDateTime,
                        status: UserStatus,
                        version: Long = 0
                ): User {
                        return User(
                                id = id,
                                createdAt = createdAt,
                                name = name,
                                email = email,
                                password = password,
                                phoneNumber = phoneNumber,
                                address = address,
                                updatedAt = updatedAt,
                                status = status,
                                version = version
                        )
                }
        }

        fun isActivate(): Boolean {
                return status == UserStatus.ACTIVE
        }

        fun changePassword(newPassword: Password): User {
                return this.copy(password = newPassword, updatedAt = LocalDateTime.now())
        }

        fun deactivateUser(): User {
                return this.copy(status = UserStatus.INACTIVE, updatedAt = LocalDateTime.now())
        }

        fun modifyUser(newName: UserName?, newAddress: Address?): User {
                return this.copy(
                        name = newName ?: this.name,
                        address = newAddress ?: this.address,
                        updatedAt = LocalDateTime.now()
                )
        }
}
