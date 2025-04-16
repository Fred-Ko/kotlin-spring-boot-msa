package com.restaurant.domain.account.exception

/**
 * 도메인 예외의 기본 클래스
 */
abstract class DomainException(
    override val message: String,
) : RuntimeException(message)
