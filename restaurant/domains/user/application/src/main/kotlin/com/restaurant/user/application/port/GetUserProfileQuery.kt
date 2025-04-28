package com.restaurant.user.application.port.`in`

import com.restaurant.user.application.dto.query.GetUserProfileByIdQuery
import com.restaurant.user.application.dto.query.UserProfileDto

/**
 * 사용자 프로필 조회 Query UseCase 인터페이스 (Rule App-Struct)
 */
interface GetUserProfileQuery {
    fun getUserProfile(query: GetUserProfileByIdQuery): UserProfileDto
}
