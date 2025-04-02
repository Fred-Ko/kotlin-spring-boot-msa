package com.restaurant.infrastructure.account.repository

import com.restaurant.infrastructure.account.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    /**
     * 계좌를 최근 N개의 트랜잭션만 포함하여 조회합니다.
     */
    @Query(
        """
        SELECT a FROM AccountEntity a
        LEFT JOIN FETCH a.transactions t
        WHERE a.id = :accountId
        ORDER BY t.timestamp DESC
    """,
    )
    fun findByIdWithRecentTransactions(
        @Param("accountId") accountId: Long,
    ): AccountEntity?
}
