package com.restaurant.infrastructure.account.entity.extensions

import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.UserId
import com.restaurant.infrastructure.account.entity.AccountEntity

/**
 * AccountEntity -> Account 도메인 객체 변환
 */
fun AccountEntity.toDomain(): Account {
    requireNotNull(this.id) { "AccountEntity ID는 null일 수 없습니다." }
    return Account.reconstitute(
        id = AccountId.of(this.id),
        userId = UserId.of(this.userId),
        balance = Money.of(this.balance),
        // version은 도메인 객체에서 제거됨
    )
}

/**
 * Account 도메인 객체 -> AccountEntity 변환
 */
fun Account.toEntity(): AccountEntity =
    AccountEntity(
        id = this.id.value, // 도메인 객체의 ID는 non-null
        userId = this.userId.value,
        balance = this.balance.value,
        // version은 Entity 생성자에서 기본값 처리 또는 DB에서 관리
    )
