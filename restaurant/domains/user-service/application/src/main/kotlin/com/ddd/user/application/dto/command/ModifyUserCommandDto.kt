package com.ddd.user.application.dto.command

data class ModifyUserCommandDto(
        val id: String,
        val name: String,
        val phoneNumber: String,
        val street: String,
        val city: String,
        val state: String,
        val zipCode: String,
)
