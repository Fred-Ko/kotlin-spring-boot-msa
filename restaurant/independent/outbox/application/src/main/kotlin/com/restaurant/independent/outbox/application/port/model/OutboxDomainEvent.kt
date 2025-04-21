package com.restaurant.independent.outbox.application.port.model

/**
 * Outbox를 통해 발행될 도메인 이벤트를 나타내는 인터페이스.
 * 이 인터페이스는 도메인 이벤트가 Outbox 메시지로 변환되기 위해 필요한 정보를 정의합니다.
 */
interface OutboxDomainEvent {
    /**
     * 이벤트가 발행될 Kafka 토픽을 반환합니다.
     */
    fun getTopic(): String

    /**
     * 이벤트의 헤더 정보를 반환합니다.
     * 헤더에는 correlationId, aggregateType, aggregateId 등이 포함될 수 있습니다.
     */
    fun getHeaders(): Map<String, String>

    /**
     * 이벤트 페이로드를 바이트 배열로 직렬화하여 반환합니다.
     * 이 메서드는 이벤트 객체를 Avro 또는 다른 형식으로 직렬화해야 합니다.
     */
    fun serialize(): ByteArray
}
