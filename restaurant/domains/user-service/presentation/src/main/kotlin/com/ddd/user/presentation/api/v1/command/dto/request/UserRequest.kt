package com.ddd.user.presentation.api.v1.command.dto.request

data class CreateUserRequest(
        val email: String,
        val password: String,
        val name: String,
        val phoneNumber: String,
        val address: AddressRequest
) {
        data class AddressRequest(
                val street: String,
                val city: String,
                val state: String,
                val zipCode: String
        )
}

data class UpdateUserRequest(
        val email: String?,
        val password: String?,
        val name: String?,
        val phoneNumber: String?,
        val address: AddressRequest?
) {
        data class AddressRequest(
                val street: String,
                val city: String,
                val state: String,
                val zipCode: String
        )
}
