package com.restaurant.domain.account.exception

import com.restaurant.domain.account.vo.TransactionId

/**
 * 트랜잭션을 찾을 수 없을 때 발생하는 예외
 */
class TransactionNotFoundException(
    val transactionId: TransactionId,
) : AccountDomainException("거래내역을 찾을 수 없습니다: ${transactionId.value}")
