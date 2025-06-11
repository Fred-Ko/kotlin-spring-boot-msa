package com.restaurant.apps.account.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant
import java.util.UUID

/**
 * Custom serializer for UUID
 */
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: UUID,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}

/**
 * Custom serializer for Instant
 */
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}

@Configuration
class SerializationConfig {
    @Bean
    fun kotlinJson(): Json =
        Json {
            // UUID와 Instant 직렬화기 등록
            serializersModule =
                SerializersModule {
                    contextual(UUIDSerializer)
                    contextual(InstantSerializer)
                }

            // 필요한 경우 kotlinx.serialization.json.Json 설정을 추가합니다.
            // 예: ignoreUnknownKeys = true, prettyPrint = false 등
            // 현재 AccountEvent.kt의 kotlinx.serialization 어노테이션과 호환되도록 기본 설정을 사용합니다.
            isLenient = true // JSON 포맷이 조금 유연해야 할 경우
            ignoreUnknownKeys = true // 스키마에 없는 키가 있어도 무시
            // explicitNulls = false // null 값을 명시적으로 보내지 않을 경우 (스키마 호환성 확인 필요)
        }
}
