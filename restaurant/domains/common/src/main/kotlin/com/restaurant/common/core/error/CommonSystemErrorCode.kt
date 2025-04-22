package com.restaurant.common.core.error

/**
 * 일반적인 시스템 수준의 오류에 대한 오류 코드 정의
 * 규칙 67에 따라 시스템 오류 코드 정의
 */
enum class CommonSystemErrorCode(
    override val code: String,
    override val message: String,
) : ErrorCode {
    SYSTEM_ERROR("COMMON-SYSTEM-999", "An unexpected system error occurred."),
    CONCURRENCY_FAILURE("COMMON-SYSTEM-500", "Concurrency conflict. Please try again."),
    EXTERNAL_SERVICE_ERROR("COMMON-SYSTEM-501", "External service call failed."),
    DATABASE_ERROR("COMMON-SYSTEM-502", "Database operation failed."),
    NETWORK_ERROR("COMMON-SYSTEM-503", "Network communication error."),
    RESOURCE_NOT_FOUND("COMMON-SYSTEM-404", "Requested resource could not be found."),
    VALIDATION_ERROR("COMMON-SYSTEM-400", "Input validation failed."),
}
