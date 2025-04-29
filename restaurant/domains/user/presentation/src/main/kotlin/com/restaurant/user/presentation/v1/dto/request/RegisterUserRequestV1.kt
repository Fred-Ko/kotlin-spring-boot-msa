/**
 * 사용자 등록 요청 DTO
 *
 * Contains validation and schema annotations for user registration API.
 *
 * @author junoko
 */
package com.restaurant.user.presentation.v1.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "사용자 등록 요청")
data class RegisterUserRequestV1(
    @Schema(description = "사용자 이메일", example = "test@example.com")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Email(message = "유효한 이메일 형식이 아닙니다.")
    val email: String,
    @Schema(description = "비밀번호", example = "password123!")
    @field:NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    val password: String,
    @Schema(description = "사용자 이름", example = "홍길동")
    @field:NotBlank(message = "이름은 필수 입력 항목입니다.")
    val name: String,
    @Schema(description = "사용자 아이디", example = "testuser")
    @field:NotBlank(message = "사용자 아이디는 필수 입력 항목입니다.")
    @field:Size(min = 3, max = 20, message = "사용자 아이디는 3자 이상 20자 이하이어야 합니다.")
    val username: String,
    @Schema(description = "전화번호 (선택)", example = "010-1234-5678")
    @field:Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다 (예: 010-1234-5678)")
    val phoneNumber: String? = null,
)
