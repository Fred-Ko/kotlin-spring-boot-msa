plugins {
    kotlin("jvm")
}

dependencies {
    api("jakarta.validation:jakarta.validation-api:3.1.1")
    api("org.springframework.boot:spring-boot-starter-web:3.4.5")
    api("org.springframework.boot:spring-boot-starter-hateoas:3.4.5")
    api("org.springframework.boot:spring-boot-starter-security:3.4.5")
    api("org.springframework.security:spring-security-crypto:6.4.5")
    api("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-avro-serializer:7.9.0")
    
    // Test dependencies are handled by subprojects block
    // testImplementation("io.kotest:kotest-runner-junit5")
    // testImplementation("io.kotest:kotest-assertions-core")
    // testImplementation("io.mockk:mockk")
}
