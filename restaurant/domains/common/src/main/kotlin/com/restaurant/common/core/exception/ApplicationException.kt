package com.restaurant.common.core.exception

import com.restaurant.common.core.error.ErrorCode

/**
 * 애플리케이션 레이어에서 발생하는 기술적 또는 외부 요인 관련 예외의 기본 클래스.
 */
abstract class ApplicationException(
    message: String,
    cause: Throwable? = null, // 원인 예외를 포함할 수 있도록 cause 추가
) : RuntimeException(message, cause) {
    /**
     * 이 예외에 해당하는 구체적인 에러 코드.
     * 각 하위 예외 클래스에서 구현해야 함 (Rule 68).
     */
    abstract val errorCode: ErrorCode
}
