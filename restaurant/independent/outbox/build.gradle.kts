plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    // REMOVED: Dependencies belong in submodules
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.5")
    implementation("org.springframework.kafka:spring-kafka:3.1.2")
    runtimeOnly("com.h2database:h2:2.2.224")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
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
