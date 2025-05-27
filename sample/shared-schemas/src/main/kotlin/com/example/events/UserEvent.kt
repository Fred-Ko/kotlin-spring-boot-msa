package com.example.events

import kotlinx.serialization.Serializable

@Serializable
data class UserEvent(
    val id: String,
    val eventType: UserEventType,
    val userId: String,
    val userName: String,
    val email: String,
    val timestamp: Long
)

enum class UserEventType {
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED
}
