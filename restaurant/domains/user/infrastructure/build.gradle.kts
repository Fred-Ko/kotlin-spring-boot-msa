plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") // For JPA Entities
    kotlin("plugin.allopen") // For JPA Entities
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" // For Avro4k kotlinx.serialization
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:infrastructure")) // For BaseEntity if needed
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:application")) // For DTOs if any indirect use
    implementation(project(":independent:outbox")) // For OutboxMessageRepository interface

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("io.github.oshai:kotlin-logging-jvm") // Logging

    // For Avro4k and Kafka
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3") // kotlinx.serialization core
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // For JSON if needed, Avro4k uses core
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:2.1.0") // Avro4k core

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
} 