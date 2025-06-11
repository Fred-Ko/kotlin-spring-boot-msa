package com.restaurant.account.infrastructure.repository

import com.restaurant.account.infrastructure.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface SpringDataJpaAccountRepository : JpaRepository<AccountEntity, Long> {
    fun findByDomainId(domainId: UUID): Optional<AccountEntity>

    fun findByUserId(userId: UUID): Optional<AccountEntity>
}
