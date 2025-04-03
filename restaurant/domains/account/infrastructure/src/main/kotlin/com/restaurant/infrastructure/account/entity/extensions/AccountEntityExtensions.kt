package com.restaurant.infrastructure.account.entity.extensions

import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.UserId
import com.restaurant.infrastructure.account.entity.AccountEntity

/**
 * AccountEntity -> Account 도메인 객체 변환
 */
fun AccountEntity.toDomain(): Account =
    Account(
        id = this.id?.let { AccountId.of(it) },
        userId = UserId.of(this.userId),
        balance = Money.of(this.balance),
    )

/**
 * Account 도메인 객체 -> AccountEntity 변환
 */
fun Account.toEntity(): AccountEntity =
    AccountEntity(
        id = this.id?.value,
        userId = this.userId.value,
        balance = this.balance.value,
    )
