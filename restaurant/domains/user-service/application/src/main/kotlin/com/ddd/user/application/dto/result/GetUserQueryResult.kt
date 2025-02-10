package com.ddd.user.application.dto.result

import java.util.UUID

data class GetUserQueryResult(
        val id: UUID,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val street: String,
        val city: String,
        val state: String,
        val zipCode: String,
        val active: Boolean
)
