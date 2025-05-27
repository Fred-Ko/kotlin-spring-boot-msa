package com.example.jsonkafkademo

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    @JsonProperty(required = false) val name: String?,
    val language: String? = null,
    val description: String? = null // Added for schema evolution test
)
