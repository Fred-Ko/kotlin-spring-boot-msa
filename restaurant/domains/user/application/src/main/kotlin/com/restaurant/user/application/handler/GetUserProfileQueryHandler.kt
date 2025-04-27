package com.restaurant.user.application.handler

import com.restaurant.user.application.dto.query.GetUserProfileByIdQuery
import com.restaurant.user.application.dto.query.UserProfileDto
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.`in`.GetUserProfileQuery
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.infrastructure.persistence.repository.SpringDataJpaUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class GetUserProfileQueryHandler(
    private val userJpaRepository: SpringDataJpaUserRepository,
) : GetUserProfileQuery {
    @Transactional(readOnly = true)
    override fun getUserProfile(query: GetUserProfileByIdQuery): UserProfileDto {
        log.debug { "Fetching user profile for userId=${query.userId}" }

        try {
            val userIdVo = UserId.fromUUID(query.userId)
            val userEntity = userJpaRepository.findByUserIdOrThrow(userIdVo.value)

            val dto =
                UserProfileDto(
                    id = userEntity.userId.toString(),
                    email = userEntity.email,
                    name = userEntity.name,
                    username = userEntity.username,
                    phoneNumber = userEntity.phoneNumber,
                    userType = userEntity.userType.name,
                    userStatus = userEntity.status.name,
                    addresses =
                        userEntity.addresses.map { addr ->
                            UserProfileDto.AddressDto(
                                id = addr.addressId.toString(),
                                street = addr.street,
                                detail = addr.detail,
                                zipCode = addr.zipCode,
                                isDefault = addr.isDefault,
                            )
                        },
                    createdAt = userEntity.createdAt,
                    updatedAt = userEntity.updatedAt,
                    version = userEntity.version,
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
