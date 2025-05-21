/**
 * build.gradle.kts for the root project.
 *
 * Configures plugins, dependency management, and global build settings.
 *
 * @author junoko
 */

plugins {
    // Only versions are declared here for subprojects
    id("org.jetbrains.kotlin.jvm") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" apply false
    id("org.springframework.boot") version "3.2.3" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}