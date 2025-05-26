package com.restaurant.user.application.query.usecase

import com.restaurant.user.application.query.dto.GetUserProfileByIdQuery
import com.restaurant.user.application.query.dto.UserProfileDto

/**
 * 사용자 프로필 조회 Query UseCase 인터페이스 (Rule App-Struct)
 */
interface GetUserProfileQuery {
    fun getUserProfile(query: GetUserProfileByIdQuery): UserProfileDto
}
