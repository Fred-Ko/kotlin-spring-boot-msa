import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    // Common plugins (jvm, spring, dependency-management, java-library) applied via subprojects block in root
    id("org.jetbrains.kotlin.plugin.serialization") // Required for Avro DTOs
}

dependencies {
    // Module dependencies
    api(project(":domains:user:domain")) // Expose domain events if needed? Use implementation.
    implementation(project(":domains:common")) // For common exceptions, ErrorCode interface etc.
    implementation(project(":domains:common:infrastructure")) // For Envelope DTO
    implementation(project(":independent:outbox:port")) // To create OutboxMessage DTO

    // Serialization
    implementation(libs.avro4k.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json) // If JSON is used by converter/factory

    // Spring
    implementation(libs.spring.context) // For @Component, @Value etc.
    // implementation(libs.spring.kafka) // Only if using KafkaTemplate directly here

    // Common libs (kotlin, slf4j, jackson, test libs) provided by root subprojects block
}

// Disable Java compile task if no Java sources are present
tasks.withType<JavaCompile> {
    enabled = false
}

// Ensure Kotlin compiler settings if not fully covered by root
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17" // Match root build.gradle.kts setting
    }
} 