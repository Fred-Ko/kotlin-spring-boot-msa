plugins {
    kotlin("jvm")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    api("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.5")
    implementation("org.springframework.security:spring-security-crypto:6.4.5")
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-avro-serializer:7.9.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    // Test dependencies are handled by subprojects block
    // testImplementation("io.kotest:kotest-runner-junit5")
    // testImplementation("io.kotest:kotest-assertions-core")
    // testImplementation("io.mockk:mockk")
}

sourceSets["main"].java.srcDir("build/generated-main-avro-java")
