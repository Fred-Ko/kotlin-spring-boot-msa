package com.restaurant.domain.account.exception

import com.restaurant.common.core.exception.DomainException

/**
 * Account 도메인의 기본 예외 클래스
 */
open class AccountDomainException(
    message: String,
) : DomainException(message)
