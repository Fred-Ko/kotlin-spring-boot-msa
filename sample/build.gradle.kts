/**
 * build.gradle.kts for the root project.
 *
 * Configures plugins, dependency management, and global build settings.
 *
 * @author junoko
 */

plugins {
    // Only versions are declared here for subprojects
    kotlin("jvm") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

// Force Kotlin versions to 2.1.0 for all configurations
subprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
            force("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
            force("org.jetbrains.kotlin:kotlin-metadata-jvm:2.1.0")
        }
    }
}