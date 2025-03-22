package com.ddd.user.application.dto.command

import java.util.UUID

data class ModifyUserDto(
        val id: UUID,
        val name: String,
        val phoneNumber: String,
        val street: String,
        val city: String,
        val state: String,
        val zipCode: String,
)
