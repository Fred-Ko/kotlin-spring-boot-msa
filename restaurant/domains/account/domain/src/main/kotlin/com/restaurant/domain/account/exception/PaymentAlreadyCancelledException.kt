package com.restaurant.domain.account.exception

import com.restaurant.domain.account.vo.TransactionId

/**
 * 이미 취소된 결제에 대한 예외
 */
class PaymentAlreadyCancelledException(
    val transactionId: TransactionId,
) : AccountDomainException("이미 취소된 결제입니다: ${transactionId.value}")
