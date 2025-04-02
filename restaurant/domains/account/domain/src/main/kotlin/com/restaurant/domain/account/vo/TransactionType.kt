package com.restaurant.domain.account.vo

/**
 * 거래 유형 Value Object
 */
enum class TransactionType {
    DEBIT, // 출금 (계좌에서 차감)
    CREDIT, // 입금 (계좌로 추가)
}
