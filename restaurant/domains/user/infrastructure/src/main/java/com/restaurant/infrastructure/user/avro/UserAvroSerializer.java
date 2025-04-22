package com.restaurant.infrastructure.user.avro;

import com.restaurant.domain.user.aggregate.User;
import java.time.Instant;

/**
 * Stub implementation for Avro serialization (only for build purposes)
 */
public class UserAvroSerializer {
    /**
     * Creates a serialized representation of User event payload
     * @param user The user object to serialize
     * @return Serialized bytes
     */
    public static byte[] serializeUser(User user) {
        // Stub implementation for build to pass
        return new byte[0];
    }

    /**
     * Creates event envelope bytes
     * @param eventId The event ID
     * @param timestamp Event timestamp
     * @param source Event source
     * @param aggregateType Type of aggregate
     * @param aggregateId ID of aggregate
     * @param eventType Type of event
     * @param payloadBytes Event payload bytes
     * @return Serialized envelope bytes
     */
    public static byte[] createEnvelopeBytes(
            String eventId,
            Instant timestamp,
            String source,
            String aggregateType,
            String aggregateId,
            String eventType,
            byte[] payloadBytes
    ) {
        // Stub implementation for build to pass
        return new byte[0];
    }
} 