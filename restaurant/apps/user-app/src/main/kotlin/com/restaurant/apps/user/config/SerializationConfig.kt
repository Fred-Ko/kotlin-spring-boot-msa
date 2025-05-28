package com.restaurant.apps.user.config

import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SerializationConfig {
    @Bean
    fun kotlinJson(): Json =
        Json {
            // 필요한 경우 kotlinx.serialization.json.Json 설정을 추가합니다.
            // 예: ignoreUnknownKeys = true, prettyPrint = false 등
            // 현재 UserEvent.kt의 kotlinx.serialization 어노테이션과 호환되도록 기본 설정을 사용합니다.
            // UserEvent의 @SerialName 등이 스키마와 일치하는지 중요합니다.
            isLenient = true // JSON 포맷이 조금 유연해야 할 경우
            ignoreUnknownKeys = true // 스키마에 없는 키가 있어도 무시
            // explicitNulls = false // null 값을 명시적으로 보내지 않을 경우 (스키마 호환성 확인 필요)
        }
}
