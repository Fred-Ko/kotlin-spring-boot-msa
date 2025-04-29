/*
 * Copyright (c) 2025 junoko. All rights reserved.
 *
 * This file is part of the user-app module.
 */

plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm")
    kotlin("plugin.spring")
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    // Core Application Dependencies
    implementation(project(":domains:common"))
    implementation(project(":domains:user:presentation"))
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:infrastructure:persistence"))
    implementation(project(":domains:user:infrastructure:messaging"))
    implementation(project(":independent:outbox"))
    implementation("org.apache.avro:avro:1.11.3")

    // Spring Boot Starter Web
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")

    // Actuator for monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.5")

    // Springdoc OpenAPI for Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Other potential dependencies
    // implementation(libs.spring.cloud.starter.config) // If using Spring Cloud Config
    // implementation(libs.micrometer.registry.prometheus) // Example Micrometer registry

    // H2 Console (개발/테스트용)
    runtimeOnly("com.h2database:h2:2.2.224")
}

// Spring Boot specific configurations
springBoot {
    mainClass.set("com.restaurant.apps.user.UserApplication")
}

// Ensure the bootJar task is configured
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = true
    // classifier = "boot"
}

// Optionally disable the plain JAR task
tasks.withType<Jar> {
    enabled = false
}
