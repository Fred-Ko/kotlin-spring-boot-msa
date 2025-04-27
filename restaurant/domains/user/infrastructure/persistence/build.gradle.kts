import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    // Common plugins (jvm, spring, dependency-management, java-library) applied via subprojects block in root
    id("org.jetbrains.kotlin.plugin.jpa") // Required for JPA entities
    id("org.jetbrains.kotlin.plugin.allopen") // Required for JPA entities
}

// Configure allopen for JPA entities
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    // Module dependencies
    api(project(":domains:user:domain")) // Expose domain types if needed by application layer through this module? Use implementation if not.
    implementation(project(":domains:common")) // For common exceptions, ErrorCode interface etc.
    implementation(project(":independent:outbox:port")) // To save OutboxMessage DTOs
    implementation(project(":domains:user:infrastructure:messaging")) // To use OutboxMessageFactory

    // Spring and JPA
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.tx) // For @Transactional
    implementation(libs.spring.context) // For @Repository, @Value etc.

    // Database Driver (example H2)
    runtimeOnly(libs.h2)

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