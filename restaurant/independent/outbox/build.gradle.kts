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
    implementation("org.springframework.boot:spring-boot-starter:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    implementation("org.springframework.kafka:spring-kafka:4.0.0-M2")
    implementation("org.flywaydb:flyway-core:11.8.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    implementation("io.confluent:kafka-avro-serializer:7.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    
    runtimeOnly("org.postgresql:postgresql:42.7.5")
    runtimeOnly("com.h2database:h2:2.3.232")
    
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
