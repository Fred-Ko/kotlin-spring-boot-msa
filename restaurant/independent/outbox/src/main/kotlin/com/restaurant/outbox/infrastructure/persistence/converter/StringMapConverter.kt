package com.restaurant.outbox.infrastructure.persistence.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

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
