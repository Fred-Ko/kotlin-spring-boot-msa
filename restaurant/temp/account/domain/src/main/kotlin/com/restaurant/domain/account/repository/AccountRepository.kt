package com.restaurant.domain.account.repository

import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.UserId

/**
 * 계좌 리포지토리 인터페이스
 */
interface AccountRepository {
    /**
     * ID로 계좌를 찾습니다.
     */
    fun findById(id: AccountId): Account?

    /**
     * 사용자 ID로 계좌를 찾습니다.
     */
    fun findByUserId(userId: UserId): Account?

    /**
     * 계좌를 저장합니다.
     */
    fun save(account: Account): Account

    /**
     * 계좌를 삭제합니다.
     */
    fun delete(id: AccountId)
}
