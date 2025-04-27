plugins {
    // kotlin("jvm") // Provided by subprojects block
    alias(libs.plugins.kotlin.spring) // Apply spring plugin
    alias(libs.plugins.kotlin.jpa) // Apply JPA plugin
    // Consider allopen/noarg if entities are defined here (see user infra)
}

dependencies {
    implementation(project(":independent:outbox:port"))
    // implementation(project(":domains:common")) // REMOVED: VIOLATES RULE 80/122

    implementation(libs.spring.boot.starter.data.jpa) // Use alias
    implementation(libs.spring.boot.starter) // Use alias

    implementation(libs.kafka.clients) // Use alias
    implementation("org.springframework.kafka:spring-kafka") // Keep specific Spring Kafka dependency

    // implementation("io.github.microutils:kotlin-logging-jvm") // Provided by subprojects
    // implementation("ch.qos.logback:logback-classic") // Provided by subprojects

    runtimeOnly(libs.postgresql) // Use alias
    // runtimeOnly(libs.h2)

    implementation(libs.jakarta.persistence.api) // Use alias

    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // Provided by subprojects
    // implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310") // Provided by subprojects
}

// Add allOpen/noArg configuration if JPA entities are defined in this module
// allOpen { ... }
// noArg { ... } 