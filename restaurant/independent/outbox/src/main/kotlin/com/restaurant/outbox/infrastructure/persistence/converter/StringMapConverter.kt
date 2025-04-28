package com.restaurant.outbox.infrastructure.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class StringMapConverter : AttributeConverter<Map<String, String>, String> {
    private val objectMapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, String>?): String =
        if (attribute == null || attribute.isEmpty()) {
            "{}"
        } else {
            objectMapper.writeValueAsString(attribute)
        }

    override fun convertToEntityAttribute(dbData: String?): Map<String, String> =
        if (dbData.isNullOrBlank()) {
            emptyMap()
        } else {
            objectMapper.readValue(dbData, object : TypeReference<Map<String, String>>() {})
        }
}
