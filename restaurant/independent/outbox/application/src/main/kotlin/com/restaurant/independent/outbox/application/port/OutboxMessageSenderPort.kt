package com.restaurant.independent.outbox.application.port

import com.restaurant.independent.outbox.application.port.model.OutboxMessage
import java.util.concurrent.CompletableFuture

/**
 * Outbox 메시지 전송을 위한 Port 인터페이스 (Application Layer)
 */
interface OutboxMessageSenderPort {
    /**
     * 단일 Outbox 메시지를 비동기적으로 전송한다.
     * 실제 Kafka 전송 성공 여부는 Future를 통해 확인해야 한다.
     */
    fun send(message: OutboxMessage): CompletableFuture<*>
}
