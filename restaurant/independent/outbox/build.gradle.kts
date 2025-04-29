plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    id("org.jetbrains.kotlin.plugin.allopen")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.5")
    implementation("org.springframework.kafka:spring-kafka:3.1.2")
    runtimeOnly("com.h2database:h2:2.2.224")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
    api("org.apache.avro:avro:1.11.3")
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
