package com.restaurant.account.domain.repository

import com.restaurant.account.domain.aggregate.Account
import com.restaurant.account.domain.vo.AccountId
import com.restaurant.account.domain.vo.UserId
import java.util.Optional

interface AccountRepository {
    fun save(account: Account): Account

    fun findById(id: AccountId): Optional<Account>

    fun findByUserId(userId: UserId): Optional<Account>
}
 