import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
// import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar // REMOVED: Unused import

plugins {
    // kotlin("jvm") // Provided by subprojects block
    alias(libs.plugins.kotlin.serialization) // Apply serialization plugin here
    // REMOVED: Removed unused shadow plugin import
    // REMOVED: Removed Avro plugin attempts
    // REMOVED: Removed common dependency (self)
}

dependencies {
    // Keep necessary dependencies
    // implementation(libs.kotlin.stdlib) // Provided by subprojects
    // implementation(libs.kotlin.reflect) // Provided by subprojects
    implementation(libs.kotlinx.serialization.core) // Use alias
    implementation(libs.kotlinx.serialization.json) // Use alias
    api(libs.kotlinx.datetime) // Use alias

    // Update avro4k dependency with correct group ID and latest version
    // implementation(libs.avro4k.core) // Remove old alias/dependency
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:2.3.0") // Use correct group ID and latest version

    // REMOVED: Dependency on avro-generated module
    // implementation(project(":domains:common:avro-generated"))

    // Add other necessary dependencies for common infrastructure
    // testImplementation(libs.kotlin.test) // Provided by subprojects
}

// REMOVED: All configurations related to avro4k and davidmc24 avro plugin
// REMOVED: SourceSets modifications