package com.ddd.user.application.dto.query

data class GetUserQueryResult(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val active: Boolean
) 