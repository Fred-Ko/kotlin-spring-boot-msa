import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // kotlin("jvm") // Provided by subprojects block
}

dependencies {
    // api("org.jetbrains.kotlin:kotlin-stdlib") // Provided by subprojects block
    // api("org.slf4j:slf4j-api") // Provided by subprojects block
    api(libs.jakarta.validation.api) // Keep API dependencies
    // api("io.github.microutils:kotlin-logging-jvm") // Provided by subprojects block

    // Test dependencies are handled by subprojects block
    // testImplementation("io.kotest:kotest-runner-junit5")
    // testImplementation("io.kotest:kotest-assertions-core")
    // testImplementation("io.mockk:mockk")
}
