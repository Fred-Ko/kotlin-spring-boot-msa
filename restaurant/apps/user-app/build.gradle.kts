/*
 * Copyright (c) 2025 junoko. All rights reserved.
 *
 * This file is part of the user-app module.
 */

plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    kotlin("plugin.allopen") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "com.restaurant"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:infrastructure:persistence"))
    implementation(project(":domains:user:infrastructure:messaging"))
    implementation(project(":domains:user:presentation"))
    implementation(project(":domains:common:application"))
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:infrastructure"))
    implementation(project(":domains:common:presentation"))
    implementation(project(":independent:outbox"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.testcontainers:kafka:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
}

