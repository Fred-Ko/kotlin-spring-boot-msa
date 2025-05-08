package com.restaurant.outbox.infrastructure.persistence.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringMapConverter : AttributeConverter<Map<String, String>, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, String>?): String =
        attribute?.let {
            objectMapper.writeValueAsString(it)
        } ?: "{}"

    override fun convertToEntityAttribute(dbData: String?): Map<String, String> =
        dbData?.let {
            objectMapper.readValue(it, object : TypeReference<Map<String, String>>() {})
        } ?: emptyMap()
}
