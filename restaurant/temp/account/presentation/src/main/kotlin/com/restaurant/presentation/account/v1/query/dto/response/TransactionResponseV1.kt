package com.restaurant.presentation.account.v1.query.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 트랜잭션 정보 응답 DTO
 *
 * @property id 트랜잭션 ID
 * @property accountId 계좌 ID
 * @property type 트랜잭션 타입 (DEBIT, CREDIT)
 * @property amount 금액
 * @property orderId 주문 ID
 * @property timestamp 트랜잭션 발생 시간 (밀리초)
 * @property dateTime 트랜잭션 발생 시간 (LocalDateTime)
 */
data class TransactionResponseV1(
    val id: Long,
    val accountId: Long,
    val type: String,
    val amount: BigDecimal,
    val orderId: String,
    val timestamp: Long,
    val dateTime: LocalDateTime,
)
