package com.restaurant.user.application.query.handler

import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetUserAddressesQuery
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service
import com.restaurant.user.application.query.usecase.GetUserAddressesQuery as GetUserAddressesUseCase

@Service
class GetUserAddressesQueryHandler(
    private val userRepository: UserRepository,
) : GetUserAddressesUseCase {
    override fun getUserAddresses(query: GetUserAddressesQuery): List<AddressDto> {
        try {
            val userId = UserId.ofString(query.userId)
            val user =
                userRepository.findById(userId)
                    ?: throw UserDomainException.User.NotFound(userId.toString())

            return user.addresses.map { address ->
                AddressDto(
                    id = address.addressId.value.toString(),
                    name = address.name,
                    streetAddress = address.streetAddress,
                    detailAddress = address.detailAddress,
                    city = address.city,
                    state = address.state,
                    country = address.country,
                    zipCode = address.zipCode,
                    isDefault = address.isDefault,
                    createdAt = address.createdAt,
                    updatedAt = address.updatedAt,
                    version = address.version,
                )
            }
        } catch (de: UserDomainException.User.NotFound) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid user ID format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(
                message = "Failed to fetch addresses due to an unexpected error.",
                cause = e,
            )
        }
    }
}
