package com.restaurant.user.application.query.handler

import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.application.query.dto.GetAddressByIdQuery
import com.restaurant.user.application.query.usecase.GetAddressByIdUseCase
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import org.springframework.stereotype.Service

@Service
class GetAddressByIdQueryHandler(
    private val userRepository: UserRepository,
) : GetAddressByIdUseCase {
    override fun getAddressById(query: GetAddressByIdQuery): AddressDto {
        try {
            val userId = UserId.ofString(query.userId)
            val addressId = AddressId.ofString(query.addressId)
            val user =
                userRepository.findById(userId)
                    ?: throw UserDomainException.User.NotFound(userId.toString())

            val address =
                user.addresses.find { it.addressId == addressId }
                    ?: throw UserDomainException.Address.NotFound(addressId.toString())

            return AddressDto(
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
        } catch (de: UserDomainException) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid ID format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(
                message = "Failed to fetch address due to an unexpected error.",
                cause = e,
            )
        }
    }
}
