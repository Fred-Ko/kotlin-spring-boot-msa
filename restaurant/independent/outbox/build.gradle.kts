plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    kotlin("plugin.jpa") version "2.1.20"
    kotlin("plugin.allopen") version "2.1.20"
    // id("org.jlleitschuh.gradle.ktlint")
    // id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1" // Avro plugin - Rule 106/109 위반으로 제거
}

group = "com.restaurant.independent"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    // Confluent repository for Kafka Avro Serializer etc.
    maven { url = uri("https://packages.confluent.io/maven/") }
}

// Define versions
object Versions {
    const val SPRING_BOOT = "3.3.2"
    const val KOTLIN_LOGGING = "3.0.5"
    const val MOCKK = "1.13.9"
    const val SPRING_MOCKK = "4.0.2"
}

dependencies {
    // Spring Boot
    implementation(platform("org.springframework.boot:spring-boot-dependencies:${Versions.SPRING_BOOT}"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING}")

    // Project Dependencies

    // Spring Boot Starters
    // implementation("org.springframework.boot:spring-boot-starter-validation") // Optional, as per instructions

    // Kafka & Avro
    // implementation("org.apache.avro:avro:$avroVersion")
    // implementation("io.confluent:kafka-avro-serializer:$kafkaAvroSerializerVersion")
    // implementation("io.confluent:kafka-schema-registry-client:$kafkaAvroSerializerVersion")

    // MapStruct (Optional - for DTO mapping, e.g., DomainEvent <-> Avro)
    // implementation("org.mapstruct:mapstruct:$mapstructVersion")
    // kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // Database (Test scope)
    testRuntimeOnly("com.h2database:h2")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:${Versions.MOCKK}")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("com.ninja-squad:springmockk:${Versions.SPRING_MOCKK}")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    // Add Kotest dependencies if needed
    // testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    // testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    // testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configure Kotlin JPA plugin
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

// If using MapStruct, configure kapt
// kapt {
//    correctErrorTypes = true
// }
