package com.restaurant.infrastructure.account.repository

import com.restaurant.infrastructure.account.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 계좌 JPA 리포지토리
 */
@Repository
interface JpaAccountRepository : JpaRepository<AccountEntity, Long> {
    /**
     * 사용자 ID로 계좌를 찾습니다.
     */
    fun findByUserId(userId: Long): AccountEntity?
}
