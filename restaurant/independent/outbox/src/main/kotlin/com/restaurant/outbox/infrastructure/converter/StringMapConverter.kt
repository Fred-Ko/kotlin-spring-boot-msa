package com.restaurant.outbox.infrastructure.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * Map<String, String>을 JSON 문자열로 변환하는 JPA Converter
 * Rule 83: Outbox 모듈의 Infrastructure 레이어 내 converter 패키지에 위치
 */
@Converter
class StringMapConverter : AttributeConverter<Map<String, String>, String> {
    private val objectMapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, String>?): String =
        if (attribute == null) {
            "{}"
        } else {
            try {
                objectMapper.writeValueAsString(attribute)
            } catch (e: Exception) {
                throw IllegalArgumentException("Error converting map to JSON", e)
            }
        }

    override fun convertToEntityAttribute(dbData: String?): Map<String, String> =
        if (dbData.isNullOrBlank()) {
            emptyMap()
        } else {
            try {
                objectMapper.readValue(dbData, object : TypeReference<Map<String, String>>() {})
            } catch (e: Exception) {
                throw IllegalArgumentException("Error converting JSON to map", e)
            }
        }
}
