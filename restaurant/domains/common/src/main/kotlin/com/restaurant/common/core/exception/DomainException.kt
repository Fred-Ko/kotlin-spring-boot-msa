package com.restaurant.common.core.exception

import com.restaurant.common.core.error.ErrorCode

/**
 * 도메인 비즈니스 규칙 위반 시 발생하는 기본 예외 클래스.
 */
abstract class DomainException(
    message: String,
) : RuntimeException(message) {
    /**
     * 이 예외에 해당하는 구체적인 에러 코드.
     * 각 하위 예외 클래스에서 구현해야 함 (Rule 68).
     */
    abstract val errorCode: ErrorCode
}
