package com.restaurant.user.presentation.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "사용자 주소 등록 요청")
data class RegisterAddressRequestV1(
    @field:Schema(description = "주소 이름", example = "집")
    @field:NotBlank(message = "주소 이름은 필수 입력 항목입니다.")
    val name: String,
    @field:Schema(description = "도로명 주소", example = "선릉로 433")
    @field:NotBlank(message = "도로명 주소는 필수 입력 항목입니다.")
    val street: String,
    @field:Schema(description = "상세 주소", example = "101호")
    val detail: String?,
    @field:Schema(description = "도시", example = "서울시")
    @field:NotBlank(message = "도시는 필수 입력 항목입니다.")
    val city: String,
    @field:Schema(description = "주/도", example = "서울특별시")
    @field:NotBlank(message = "주/도는 필수 입력 항목입니다.")
    val state: String,
    @field:Schema(description = "국가", example = "대한민국")
    @field:NotBlank(message = "국가는 필수 입력 항목입니다.")
    val country: String,
    @field:Schema(description = "우편번호", example = "06211")
    @field:NotBlank(message = "우편번호는 필수 입력 항목입니다.")
    @field:Size(min = 5, max = 5, message = "우편번호는 5자리여야 합니다.")
    val zipCode: String,
    @field:Schema(description = "기본 주소 여부", example = "false", defaultValue = "false")
    val isDefault: Boolean? = false,
)
