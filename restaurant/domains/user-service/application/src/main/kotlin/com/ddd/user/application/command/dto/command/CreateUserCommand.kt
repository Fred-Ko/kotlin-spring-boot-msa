package com.ddd.user.application.command.dto.command

data class CreateUserCommand(
        val email: String,
        val password: String,
        val name: Name,
        val phoneNumber: PhoneNumber,
        val address: Address
) {
        data class Address(
                val street: String,
                val city: String,
                val state: String,
                val zipCode: String
        )

        data class PhoneNumber(val number: String)

        data class Name(val name: String)
}
