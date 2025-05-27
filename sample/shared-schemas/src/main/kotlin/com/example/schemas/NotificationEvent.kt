package com.example.schemas

import kotlinx.serialization.Serializable

@Serializable
data class NotificationEvent(
    val id: String,
    val eventType: NotificationEventType,
    val userId: String,
    val title: String,
    val message: String,
    val channel: NotificationChannel,
    val priority: NotificationPriority,
    val timestamp: Long,
    val metadata: Map<String, String> = emptyMap(),
)

@Serializable
enum class NotificationEventType {
    NOTIFICATION_CREATED,
    NOTIFICATION_SENT,
    NOTIFICATION_DELIVERED,
    NOTIFICATION_FAILED,
    NOTIFICATION_READ,
}

@Serializable
enum class NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
    IN_APP,
}

@Serializable
enum class NotificationPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT,
}
