package com.restaurant.account.infrastructure.messaging.event

import kotlinx.serialization.Serializable

@Serializable
data class UserEventDeactivated(
    val type: String,
    val id: String,
    val eventId: String,
    val occurredAt: String
)