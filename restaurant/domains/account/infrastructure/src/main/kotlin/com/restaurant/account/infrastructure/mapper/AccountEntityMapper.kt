package com.restaurant.account.infrastructure.mapper

import com.restaurant.account.domain.aggregate.Account
import com.restaurant.account.domain.vo.AccountId
import com.restaurant.account.domain.vo.Balance
import com.restaurant.account.domain.vo.UserId
import com.restaurant.account.infrastructure.entity.AccountEntity

object AccountEntityMapper {
    fun toEntity(account: Account): AccountEntity =
        AccountEntity(
            id = null, // JPA가 자동 생성
            domainId = account.id.value,
            userId = account.userId.value,
            balance = account.balance.value,
            status = account.status,
            version = account.version,
        )

    fun toDomain(entity: AccountEntity): Account =
        Account(
            id = AccountId.of(entity.domainId),
            userId = UserId.of(entity.userId),
            balance = Balance.of(entity.balance),
            status = entity.status,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            version = entity.version,
        )
}
