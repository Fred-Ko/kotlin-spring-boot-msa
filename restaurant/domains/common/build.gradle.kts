plugins {
    kotlin("jvm")
}

dependencies {
    api("jakarta.validation:jakarta.validation-api:3.0.2")
    api("org.springframework.boot:spring-boot-starter-web:3.3.5")
    api("org.springframework.boot:spring-boot-starter-hateoas:3.3.5")
    api("org.springframework.boot:spring-boot-starter-security:3.3.5")
    api("org.springframework.security:spring-security-crypto:6.3.1")
    api("org.apache.avro:avro:1.12.2")
    implementation("io.confluent:kafka-avro-serializer:7.9.0")
    implementation("com.github.avro-kotlin.avro4k-core:1.9.1")

    // Test dependencies are handled by subprojects block
    // testImplementation("io.kotest:kotest-runner-junit5")
    // testImplementation("io.kotest:kotest-assertions-core")
    // testImplementation("io.mockk:mockk")
}
