package com.ddd.restaurant.application.event.handler

import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class RestaurantEventHandler {

    @TransactionalEventListener // 예시: 트랜잭션 이벤트 리스너
    fun handleRestaurantCreatedEvent() {
        // 레스토랑 생성 이벤트 처리 로직 (필요한 경우)
        println("Restaurant Created Event Received")
    }
}
