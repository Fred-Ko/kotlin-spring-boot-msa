package com.restaurant.shared.outbox.application.dto

import java.util.UUID

/**
 * Outbox polling용 DTO (infra entity 직접 참조 금지)
 */
data class OutboxEventPollingDto(
    val id: Long?,
    val eventId: UUID,
    val aggregateType: String,
    val aggregateId: String,
    val eventType: String,
    val payload: String,
    val status: String,
    val retryCount: Int,
)
