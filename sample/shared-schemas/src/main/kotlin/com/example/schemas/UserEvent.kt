package com.example.schemas

import kotlinx.serialization.Serializable

@Serializable
data class UserEvent(
    val id: String,
    val eventType: UserEventType,
    val userId: String,
    val email: String,
    val name: String,
    val timestamp: Long,
    val metadata: Map<String, String> = emptyMap(),
)

@Serializable
enum class UserEventType {
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    USER_ACTIVATED,
    USER_DEACTIVATED,
}
