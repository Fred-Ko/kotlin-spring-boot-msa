/**
 * build.gradle.kts for the root project.
 *
 * Configures plugins, dependency management, and global build settings.
 *
 * @author junoko
 */

buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://packages.confluent.io/maven/")
    }
}

plugins {
    // Only versions are declared here for subprojects
    kotlin("jvm") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.github.imflog.kafka-schema-registry-gradle-plugin") version "2.3.2" apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://packages.confluent.io/maven/")
    }
}