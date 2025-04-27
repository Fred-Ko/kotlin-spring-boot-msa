package com.restaurant.common.infrastructure.avro.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual // Add import
import java.time.Instant

/**
 * Common Envelope structure for Kafka messages.
 * Rule 113
 */
@Serializable
data class Envelope(
    val schemaVersion: String, // Rule 114
    val eventId: String, // Rule 115 (Correlation ID)
    @Contextual val timestamp: Instant, // Rule 116 (Use @Contextual for Instant)
    val source: String, // Rule 117
    val aggregateType: String, // Rule 118
    val aggregateId: String // Rule 119
    // payload: ByteArray // REMOVED - Rule 113, 120
) 