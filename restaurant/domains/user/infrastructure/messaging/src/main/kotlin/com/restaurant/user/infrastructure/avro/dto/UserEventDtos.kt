package com.restaurant.user.infrastructure.avro.dto

import kotlinx.serialization.Contextual // Import Contextual
import kotlinx.serialization.Serializable
import java.time.Instant // Import Instant

// Rule 109, 111: @Serializable Kotlin DTO 직접 작성

@Serializable
data class AddressAvroDto( // AddressData 에 대응
    val addressId: String,
    val street: String,
    val detail: String,
    val zipCode: String,
    val isDefault: Boolean,
)

@Serializable
data class UserCreatedEventDtoV1(
    val userId: String,
    val username: String,
    val email: String,
    val name: String,
    val phoneNumber: String?, // Nullable
    val userType: String, // Enum name as String
    // @Contextual // Temporarily removed
    @Contextual
    val registeredAt: Instant,
    // addresses 필드는 필요시 AddressAvroDto 리스트로 추가
    // val addresses: List<AddressAvroDto> = emptyList()
)

@Serializable
data class UserPasswordChangedEventDtoV1(
    val userId: String,
    // @Contextual // Temporarily removed
    @Contextual
    val changedAt: Instant,
)

@Serializable
data class UserProfileUpdatedEventDtoV1(
    val userId: String,
    val name: String,
    val phoneNumber: String?, // Nullable
    // @Contextual // Temporarily removed
    @Contextual
    val updatedAt: Instant,
)

@Serializable
data class UserAddressAddedEventDtoV1(
    val userId: String,
    val address: AddressAvroDto,
    // @Contextual // Temporarily removed
    @Contextual
    val addedAt: Instant,
)

@Serializable
data class UserAddressUpdatedEventDtoV1(
    val userId: String,
    val address: AddressAvroDto,
    // @Contextual // Temporarily removed
    val updatedAt: Instant,
)

@Serializable
data class UserAddressDeletedEventDtoV1( // Renamed from Removed
    val userId: String,
    val addressId: String,
    // @Contextual // Temporarily removed
    val deletedAt: Instant,
)

@Serializable
data class UserWithdrawnEventDtoV1( // Renamed from UserWithdrawn
    val userId: String,
    // @Contextual // Temporarily removed
    val withdrawnAt: Instant,
)
