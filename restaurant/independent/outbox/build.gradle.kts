plugins {
    // REMOVED: Plugins should be applied in submodules (port, internal, infrastructure) where needed
    // id("org.jetbrains.kotlin.plugin.jpa")
    // id("org.jetbrains.kotlin.plugin.allopen")
    // id("org.jetbrains.kotlin.plugin.noarg")
    kotlin("jvm") // Keep JVM plugin if common config applied here
}

dependencies {
    // REMOVED: Dependencies belong in submodules
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // implementation("org.springframework.kafka:spring-kafka")
    // implementation("org.springframework:spring-context-support")
    // implementation("jakarta.persistence:jakarta.persistence-api")
    // implementation("com.fasterxml.jackson.core:jackson-databind")
    // runtimeOnly("com.h2database:h2")
    // runtimeOnly("org.postgresql:postgresql")
    // testImplementation("org.springframework.boot:spring-boot-starter-test")
    // testImplementation("org.springframework.kafka:spring-kafka-test")
    // testImplementation("org.testcontainers:junit-jupiter")
    // testImplementation("org.testcontainers:kafka")
    // testImplementation("org.testcontainers:postgresql")
}

// REMOVED: Configuration belongs in the infrastructure submodule
// allOpen {
//     annotation("jakarta.persistence.Entity")
//     annotation("jakarta.persistence.Embeddable")
//     annotation("jakarta.persistence.MappedSuperclass")
// }
//
// noArg {
//     annotation("jakarta.persistence.Entity")
// } 