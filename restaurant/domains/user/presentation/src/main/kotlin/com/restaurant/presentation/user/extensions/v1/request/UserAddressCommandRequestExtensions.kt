package com.restaurant.presentation.user.extensions.v1.request

import com.restaurant.application.user.command.RegisterAddressCommand
import com.restaurant.application.user.command.UpdateAddressCommand
import com.restaurant.presentation.user.v1.command.dto.request.RegisterAddressRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UpdateAddressRequestV1

// RegisterAddressRequestV1 -> RegisterAddressCommand 변환
fun RegisterAddressRequestV1.toCommand(userId: String): RegisterAddressCommand =
    RegisterAddressCommand(
        userId = userId,
        street = this.street,
        detail = this.detail ?: "",
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false,
    )

// UpdateAddressRequestV1 -> UpdateAddressCommand 변환
fun UpdateAddressRequestV1.toCommand(
    userId: String,
    addressId: String,
): UpdateAddressCommand =
    UpdateAddressCommand(
        userId = userId,
        addressId = addressId,
        street = this.street,
        detail = this.detail ?: "",
        zipCode = this.zipCode,
        isDefault = this.isDefault ?: false,
    )
