package com.restaurant.shared.outbox.infrastructure.exception

/**
 * Outbox 모듈의 Infrastructure 레이어에서 발생하는 예외의 기반 클래스.
 * domains/common 의존성을 제거하기 위해 정의됨.
 */
open class OutboxBaseInfrastructureException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
