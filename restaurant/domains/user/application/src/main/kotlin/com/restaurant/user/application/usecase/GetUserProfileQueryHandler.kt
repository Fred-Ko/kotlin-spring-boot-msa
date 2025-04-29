package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.query.GetUserProfileByIdQuery
import com.restaurant.user.application.dto.query.UserProfileDto
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.input.GetUserProfileQuery
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class GetUserProfileQueryHandler(
    private val userRepository: UserRepository,
) : GetUserProfileQuery {
    @Transactional(readOnly = true)
    override fun getUserProfile(query: GetUserProfileByIdQuery): UserProfileDto {
        log.debug { "Fetching user profile for userId=${query.userId}" }

        try {
            val userIdVo = UserId.ofString(query.userId)
            val user = userRepository.findById(userIdVo) ?: throw UserDomainException.User.NotFound(userIdVo.toString())

            val dto =
                UserProfileDto(
                    id = user.id.value.toString(),
                    email = user.email.value,
                    name = user.name.value,
                    username = user.username.value,
                    phoneNumber = user.phoneNumber?.value,
                    userType = user.userType.name,
                    addresses =
                        user.addresses.map {
                            UserProfileDto.AddressDto(
                                id = it.addressId.value.toString(),
                                street = it.street,
                                detail = it.detail,
                                zipCode = it.zipCode,
                                isDefault = it.isDefault,
                            )
                        },
                    createdAt = user.createdAt,
                    updatedAt = user.updatedAt,
                    status = user.status.name,
                    version = user.version,
                )

            log.info { "User profile fetched successfully, userId=${userIdVo.value}" }
            return dto
        } catch (de: UserDomainException.User.NotFound) {
            log.warn { "User profile query failed, user not found: userId=${query.userId}" }
            throw de
        } catch (de: UserDomainException) {
            log.warn(
                de,
            ) { "Domain validation error during profile query for user ${query.userId}: code=${de.errorCode.code}, message=${de.message}" }
            throw de
        } catch (iae: IllegalArgumentException) {
            log.warn(iae) { "Invalid user ID format for query: ${query.userId}" }
            throw UserApplicationException.BadRequest("Invalid user ID format", iae)
        } catch (e: Exception) {
            log.error(e) { "Unexpected error during profile query for user ${query.userId}: ${e.message}" }
            throw UserApplicationException.UnexpectedError(cause = e)
        }
    }
}
