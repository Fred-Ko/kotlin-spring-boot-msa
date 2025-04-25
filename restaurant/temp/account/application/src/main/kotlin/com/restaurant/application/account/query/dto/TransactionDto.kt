package com.restaurant.application.account.query.dto

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * 트랜잭션 정보 DTO
 *
 * @property id 트랜잭션 ID
 * @property accountId 계좌 ID
 * @property type 트랜잭션 타입 (DEBIT, CREDIT)
 * @property amount 금액
 * @property orderId 주문 ID
 * @property timestamp 트랜잭션 발생 시간 (밀리초)
 * @property dateTime 트랜잭션 발생 시간 (LocalDateTime)
 */
data class TransactionDto(
    val id: Long,
    val accountId: Long,
    val type: String,
    val amount: BigDecimal,
    val orderId: String,
    val timestamp: Long,
) {
    // 프론트엔드에서 표시하기 쉽도록 LocalDateTime 추가
    val dateTime: LocalDateTime =
        Instant
            .ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
}
