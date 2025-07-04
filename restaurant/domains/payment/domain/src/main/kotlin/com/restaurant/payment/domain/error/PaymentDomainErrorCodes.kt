package com.restaurant.payment.domain.error

import com.restaurant.common.domain.error.ErrorCode

/**
 * Payment 도메인 비즈니스 규칙 위반 관련 에러 코드 Enum (Rule 67)
 */
enum class PaymentDomainErrorCodes(
    override val code: String,
    override val message: String,
) : ErrorCode {
    // Payment 관련 에러 코드
    PAYMENT_NOT_FOUND("PAYMENT-DOMAIN-001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_APPROVED("PAYMENT-DOMAIN-002", "이미 승인된 결제입니다."),
    PAYMENT_ALREADY_FAILED("PAYMENT-DOMAIN-003", "이미 실패한 결제입니다."),
    PAYMENT_ALREADY_REFUNDED("PAYMENT-DOMAIN-004", "이미 환불된 결제입니다."),
    PAYMENT_CANNOT_BE_REFUNDED("PAYMENT-DOMAIN-005", "환불할 수 없는 결제입니다."),
    INSUFFICIENT_REFUND_AMOUNT("PAYMENT-DOMAIN-006", "환불 가능 금액을 초과했습니다."),
    INVALID_PAYMENT_AMOUNT("PAYMENT-DOMAIN-007", "잘못된 결제 금액입니다."),

    // PaymentMethod 관련 에러 코드
    PAYMENT_METHOD_NOT_FOUND("PAYMENT-DOMAIN-011", "결제 수단을 찾을 수 없습니다."),
    PAYMENT_METHOD_ALREADY_EXISTS("PAYMENT-DOMAIN-012", "이미 등록된 결제 수단입니다."),
    PAYMENT_METHOD_LIMIT_EXCEEDED("PAYMENT-DOMAIN-013", "최대 결제 수단 등록 개수를 초과했습니다."),
    DEFAULT_PAYMENT_METHOD_CANNOT_BE_DELETED("PAYMENT-DOMAIN-014", "기본 결제 수단은 삭제할 수 없습니다."),
    CANNOT_DELETE_LAST_PAYMENT_METHOD("PAYMENT-DOMAIN-015", "마지막 결제 수단은 삭제할 수 없습니다."),

    // 유효성 검사 관련 에러 코드
    INVALID_PAYMENT_ID_FORMAT("PAYMENT-DOMAIN-101", "잘못된 결제 ID 형식입니다."),
    INVALID_PAYMENT_METHOD_ID_FORMAT("PAYMENT-DOMAIN-102", "잘못된 결제 수단 ID 형식입니다."),
    INVALID_ORDER_ID_FORMAT("PAYMENT-DOMAIN-103", "잘못된 주문 ID 형식입니다."),
    INVALID_USER_ID_FORMAT("PAYMENT-DOMAIN-104", "잘못된 사용자 ID 형식입니다."),
    INVALID_AMOUNT_FORMAT("PAYMENT-DOMAIN-105", "잘못된 금액 형식입니다."),
    INVALID_CARD_NUMBER_FORMAT("PAYMENT-DOMAIN-106", "잘못된 카드 번호 형식입니다."),
    INVALID_CARD_EXPIRY_FORMAT("PAYMENT-DOMAIN-107", "잘못된 카드 유효기간 형식입니다."),
    INVALID_CARD_CVV_FORMAT("PAYMENT-DOMAIN-108", "잘못된 카드 CVV 형식입니다."),
    INVALID_TRANSACTION_ID_FORMAT("PAYMENT-DOMAIN-109", "잘못된 거래 ID 형식입니다."),
    INVALID_ACCOUNT_NUMBER_FORMAT("PAYMENT-DOMAIN-110", "잘못된 계좌번호 형식입니다."),

    // 비즈니스 로직 관련 에러 코드
    PAYMENT_METHOD_ID_MISMATCH("PAYMENT-DOMAIN-201", "결제 수단 ID가 일치하지 않습니다."),
    PAYMENT_PROCESSING_ERROR("PAYMENT-DOMAIN-202", "결제 처리 중 오류가 발생했습니다."),
    REFUND_PROCESSING_ERROR("PAYMENT-DOMAIN-203", "환불 처리 중 오류가 발생했습니다."),
    EXTERNAL_PAYMENT_GATEWAY_ERROR("PAYMENT-DOMAIN-204", "외부 결제 게이트웨이 오류입니다."),

    // 시스템 관련 에러 코드
    PERSISTENCE_ERROR("PAYMENT-DOMAIN-901", "데이터 저장 중 오류가 발생했습니다."),
}
