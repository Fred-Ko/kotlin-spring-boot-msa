package com.example.events

import kotlinx.serialization.Serializable

@Serializable
data class OrderEvent(
    val id: String,
    val eventType: OrderEventType,
    val orderId: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val timestamp: Long
)

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)

enum class OrderEventType {
    ORDER_CREATED,
    ORDER_UPDATED,
    ORDER_COMPLETED,
    ORDER_CANCELLED
}
