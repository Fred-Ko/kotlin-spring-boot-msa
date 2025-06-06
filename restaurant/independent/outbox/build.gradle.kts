plugins {
    id("java-library")
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    // Spring Boot & JPA
    implementation("org.springframework.boot:spring-boot-starter:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    
    // Spring Kafka (KafkaTemplate 사용)
    implementation("org.springframework.kafka:spring-kafka:4.0.0-M2")
    
    // Confluent Schema Registry for JSON Schema support
    implementation("io.confluent:kafka-json-schema-serializer:7.5.1")
    implementation("io.confluent:kafka-schema-registry-client:7.5.1")
    
    // kotlinx.serialization for UserEvent (de)serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")

    // UserEvent 정의를 참조하기 위한 의존성 (제거)
    // implementation(project(":domains:user:domain"))
    
    // Database migration
    implementation("org.flywaydb:flyway-core:11.8.2")
    
    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    
    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    
    // Database drivers
    runtimeOnly("org.postgresql:postgresql:42.7.5")
    runtimeOnly("com.h2database:h2:2.3.232")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("org.springframework.kafka:spring-kafka-test:4.0.0-M2")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.testcontainers:testcontainers:1.21.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
    testImplementation("org.testcontainers:kafka:1.21.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
