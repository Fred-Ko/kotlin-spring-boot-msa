package com.restaurant.application.account.exception

import com.restaurant.common.core.exception.ApplicationException
import com.restaurant.domain.account.vo.AccountId

/**
 * 계좌 애플리케이션 예외의 부모 클래스
 */
open class AccountApplicationException(
    message: String,
) : ApplicationException(message)

/**
 * 계좌를 찾을 수 없을 때 발생하는 예외
 */
class AccountNotFoundException(
    accountId: AccountId,
) : AccountApplicationException("계좌를 찾을 수 없습니다: ${accountId.value}")

/**
 * 잔액이 부족할 때 발생하는 예외
 */
class InsufficientBalanceException(
    message: String,
) : AccountApplicationException(message)

/**
 * 계좌 트랜잭션 관련 예외
 */
class AccountTransactionException(
    message: String,
) : AccountApplicationException(message)
