package com.ddd.user.application.event

import com.ddd.user.domain.model.event.UserCreatedEventV1
import com.ddd.user.domain.model.event.UserUpdatedEventV1
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserEventHandler {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: UserCreatedEventV1) {
        // TODO: 이메일 발송, 알림 발송 등의 후처리 작업
        println("User Created: ${event.aggregateId}")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: UserUpdatedEventV1) {
        // TODO: 프로필 변경 알림 등의 후처리 작업
        println("User Updated: ${event.aggregateId}")
    }
}
