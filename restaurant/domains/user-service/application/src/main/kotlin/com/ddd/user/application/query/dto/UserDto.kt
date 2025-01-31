package com.ddd.user.application.query.dto

import com.ddd.user.domain.model.aggregate.User
import java.util.UUID

data class UserDto(
        val id: UUID,
        val name: String,
        val email: String,
        val phoneNumber: String,
        val street: String,
        val city: String,
        val state: String,
        val zipCode: String,
        val status: String
) {
    companion object {
        fun from(user: User) =
                UserDto(
                        id = user.id,
                        name = user.name.value,
                        email = user.email.value,
                        phoneNumber = user.phoneNumber.value,
                        street = user.address.street,
                        city = user.address.city,
                        state = user.address.state,
                        zipCode = user.address.zipCode,
                        status = user.status.toString()
                )
    }
}
