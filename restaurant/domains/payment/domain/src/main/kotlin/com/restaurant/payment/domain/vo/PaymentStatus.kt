package com.restaurant.payment.domain.vo

/**
 * Represents the status of a payment.
 */
enum class PaymentStatus {
    PENDING, // 결제 대기중
    APPROVED, // 결제 승인됨
    FAILED, // 결제 실패
    REFUNDED, // 환불됨
    CANCELLED, // 취소됨
}
