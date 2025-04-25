package com.restaurant.infrastructure.account.entity.extensions

import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.UserId
import com.restaurant.infrastructure.account.entity.AccountEntity

/**
 * AccountEntity를 도메인 Account로 변환
 */
fun AccountEntity.toDomain(): Account =
    Account.reconstitute(
        id = AccountId.of(id!!),
        userId = UserId.of(userId),
        balance = Money.of(balance),
    )

/**
 * Account를 AccountEntity로 변환
 */
fun Account.toEntity(): AccountEntity =
    AccountEntity(
        id = id.value,
        userId = userId.value,
        balance = balance.value,
    )
