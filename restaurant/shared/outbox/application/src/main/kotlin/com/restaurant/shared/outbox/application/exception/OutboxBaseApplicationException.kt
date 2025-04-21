package com.restaurant.shared.outbox.application.exception

import com.restaurant.shared.outbox.application.error.OutboxErrorCode

/**
 * Outbox 모듈의 Application 레이어에서 발생하는 예외의 기반 클래스.
 * domains/common 의존성을 제거하기 위해 정의됨.
 */
abstract class OutboxBaseApplicationException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    /**
     * 이 예외에 해당하는 구체적인 Outbox 에러 코드.
     */
    abstract val errorCode: OutboxErrorCode
}
