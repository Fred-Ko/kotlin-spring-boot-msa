/**
 * build.gradle.kts for the root project.
 *
 * Configures plugins, dependency management, and global build settings.
 *
 * @author junoko
 */

plugins {
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    kotlin("jvm") version "2.0.20" apply false
    kotlin("plugin.spring") version "2.0.20" apply false
    kotlin("plugin.jpa") version "2.0.20" apply false
    kotlin("plugin.allopen") version "2.0.20" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1" apply false
}

allprojects {
    group = "com.restaurant"
    version = "0.0.1-SNAPSHOT"
}

ktlint {
    version.set("1.2.1")
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
    }
}
