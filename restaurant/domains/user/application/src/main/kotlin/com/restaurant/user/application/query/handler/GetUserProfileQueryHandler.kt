package com.restaurant.user.application.query.handler

import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.query.dto.GetUserProfileByIdQuery
import com.restaurant.user.application.query.dto.UserProfileDto
import com.restaurant.user.application.query.usecase.GetUserProfileQuery
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserProfileQueryHandler(
    private val userRepository: UserRepository,
) : GetUserProfileQuery {
    @Transactional(readOnly = true)
    override fun getUserProfile(query: GetUserProfileByIdQuery): UserProfileDto {
        try {
            val userId = UserId.ofString(query.userId)
            val user = userRepository.findById(userId) ?: throw UserDomainException.User.NotFound(userId.toString())

            return UserProfileDto(
                id = user.id.value.toString(),
                username = user.username.value,
                email = user.email.value,
                name = user.name.value,
                phoneNumber = user.phoneNumber?.value,
                addresses =
                    user.addresses.map { address ->
                        UserProfileDto.AddressDto(
                            id = address.addressId.value.toString(),
                            name = address.name,
                            streetAddress = address.streetAddress,
                            detailAddress = address.detailAddress,
                            city = address.city,
                            state = address.state,
                            country = address.country,
                            zipCode = address.zipCode,
                            isDefault = address.isDefault,
                        )
                    },
                userType = user.userType.name,
                status = user.status.name,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                version = user.version,
            )
        } catch (de: UserDomainException.User.NotFound) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid user ID format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(message = "Failed to fetch profile due to an unexpected error.", cause = e)
        }
    }
}
