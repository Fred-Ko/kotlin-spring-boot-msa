package com.example.schemas

import kotlinx.serialization.Serializable

@Serializable
data class OrderEvent(
    val id: String,
    val eventType: OrderEventType,
    val orderId: String,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val price: Double,
    val status: OrderStatus,
    val timestamp: Long,
    val metadata: Map<String, String> = emptyMap(),
)

@Serializable
enum class OrderEventType {
    ORDER_CREATED,
    ORDER_UPDATED,
    ORDER_CANCELLED,
    ORDER_COMPLETED,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
}

@Serializable
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
}
