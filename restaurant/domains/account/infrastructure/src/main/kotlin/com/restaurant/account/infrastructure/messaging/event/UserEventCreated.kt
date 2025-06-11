package com.restaurant.account.infrastructure.messaging.event

import kotlinx.serialization.Serializable

@Serializable
data class UserEventCreated(
    val type: String,
    val id: String,
    val eventId: String,
    val occurredAt: String,
    val username: String,
    val email: String,
    val name: String,
    val phoneNumber: String? = null,
    val userType: String
)