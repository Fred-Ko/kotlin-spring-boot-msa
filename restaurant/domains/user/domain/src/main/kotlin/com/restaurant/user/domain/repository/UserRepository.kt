package com.restaurant.user.domain.repository

import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username

/**
 * 사용자 도메인 리포지토리 Port (Domain Layer).
 * 애플리케이션 코어 로직은 이 인터페이스에 의존한다.
 * 실제 구현은 Infrastructure 레이어에서 제공한다.
 */
interface UserRepository {
    /**
     * 사용자를 저장하거나 업데이트한다.
     * @param user 저장 또는 업데이트할 User Aggregate
     * @return 저장된 User Aggregate (Immutable Aggregate 패턴 고려 시 새 인스턴스)
     */
    fun save(user: User): User

    /**
     * 사용자 ID로 사용자를 찾는다.
     * @param id 사용자 ID (Value Object)
     * @return 찾은 User Aggregate 또는 null
     */
    fun findById(id: UserId): User?

    /**
     * 사용자 이름으로 사용자를 찾는다.
     * @param username 사용자 이름 (Value Object)
     * @return 찾은 User Aggregate 또는 null
     */
    fun findByUsername(username: Username): User?

    /**
     * 이메일로 사용자를 찾는다.
     * @param email 이메일 (Value Object)
     * @return 찾은 User Aggregate 또는 null
     */
    fun findByEmail(email: Email): User?

    fun existsByEmail(email: Email): Boolean

    fun existsByUsername(username: Username): Boolean

    fun delete(user: User)
}
