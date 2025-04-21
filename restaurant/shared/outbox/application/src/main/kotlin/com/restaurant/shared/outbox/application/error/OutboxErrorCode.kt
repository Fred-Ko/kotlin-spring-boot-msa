package com.restaurant.shared.outbox.application.error

import org.springframework.http.HttpStatus

/**
 * Outbox 모듈 내에서 사용될 오류 코드 인터페이스.
 * domains/common 의존성을 제거하기 위해 정의됨.
 */
interface OutboxErrorCode {
    val code: String
    val message: String
    val status: HttpStatus // Outbox 자체 오류는 HTTP 상태와 직접 관련 없을 수 있으나, 기존 코드 호환성을 위해 유지
}
