package com.restaurant.common.core.error

/**
 * 모든 에러 코드가 구현해야 하는 공통 인터페이스
 */
interface ErrorCode {
    /**
     * 에러 코드 (예: USER-001)
     */
    val code: String

    /**
     * 에러 메시지
     */
    val message: String
}

abstract class BaseErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    companion object {
        fun fromCode(
            errorCodes: List<ErrorCode>,
            code: String?,
        ): ErrorCode? = errorCodes.find { it.code == code }
    }
}
