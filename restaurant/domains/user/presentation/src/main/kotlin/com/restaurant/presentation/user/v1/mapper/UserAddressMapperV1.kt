package com.restaurant.presentation.user.v1.mapper

import com.restaurant.application.user.command.DeleteAddressCommand
import com.restaurant.application.user.command.RegisterAddressCommand
import com.restaurant.application.user.command.UpdateAddressCommand
import com.restaurant.domain.user.vo.Address
import com.restaurant.presentation.user.v1.command.dto.request.UserAddressRegisterRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UserAddressUpdateRequestV1
import com.restaurant.presentation.user.v1.query.dto.response.AddressResponseV1
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserAddressMapperV1 {
  @Mapping(target = "userId", source = "userId")
  fun toRegisterAddressCommand(
    userId: Long,
    request: UserAddressRegisterRequestV1,
  ): RegisterAddressCommand

  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "addressId", source = "addressId")
  fun toUpdateAddressCommand(
    userId: Long,
    addressId: Long,
    request: UserAddressUpdateRequestV1,
  ): UpdateAddressCommand

  @Mapping(target = "userId", source = "userId")
  @Mapping(target = "addressId", source = "addressId")
  fun toDeleteAddressCommand(
    userId: Long,
    addressId: Long,
  ): DeleteAddressCommand

  fun toAddressResponseV1(address: Address): AddressResponseV1

  fun toAddressResponseList(addresses: List<Address>): List<AddressResponseV1>
}
