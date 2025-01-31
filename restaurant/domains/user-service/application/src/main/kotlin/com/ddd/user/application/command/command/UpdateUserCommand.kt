package com.ddd.user.application.command.command

import com.ddd.user.domain.model.vo.Address
import java.util.UUID

data class UpdateUserCommand(
        val id: UUID,
        val email: String? = null,
        val password: String? = null,
        val name: String? = null,
        val phoneNumber: String? = null,
        val address: Address? = null
) {
        data class Address(
                val street: String,
                val city: String,
                val state: String,
                val zipCode: String
        )
}
