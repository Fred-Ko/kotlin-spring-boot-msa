plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0" // For JPA Entities
    kotlin("plugin.allopen") version "2.1.0" // For JPA Entities
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management")
}

extra["springCloudVersion"] = "2023.0.1" // Spring Cloud 2023.0.1 is compatible with Spring Boot 3.2.x

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Force kotlinx-serialization versions before dependencies
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
        force("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.8.1")
        force("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
        force("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.8.1")

        eachDependency {
            if (requested.group == "org.jetbrains.kotlinx" && requested.name.contains("serialization")) {
                if (requested.name.contains("bom")) {
                    // Skip BOM entirely
                    return@eachDependency
                }
                useVersion("1.8.1")
            }
        }
    }

    // Spring Cloud Function is required for StreamBridge to work properly
    // Previously excluded but needed for StreamBridge
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:application"))
    implementation(project(":domains:common:infrastructure"))
    implementation(project(":domains:common:presentation"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:infrastructure"))
    implementation(project(":domains:user:presentation"))
    implementation(project(":independent:outbox"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Spring Cloud Stream with Function
    implementation("org.springframework.cloud:spring-cloud-stream")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-kafka-streams")
    implementation("org.springframework.cloud:spring-cloud-function-context")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    implementation("org.flywaydb:flyway-core")

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-kotlin")
    implementation("io.github.resilience4j:resilience4j-spring-boot3")

    // UUID Generator
    implementation("com.fasterxml.uuid:java-uuid-generator:5.1.0")
    implementation("io.confluent:kafka-avro-serializer:7.5.0")
    // implementation("io.confluent:kafka-json-schema-serializer:7.5.0")
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:2.3.0")

    // SpringDoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Swagger Annotations
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.31")
    implementation("org.webjars:swagger-ui:5.22.0")

    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
    testImplementation("org.testcontainers:kafka:1.21.0")
    testImplementation("org.awaitility:awaitility:4.2.1")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.bootJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.bootRun {
    jvmArgs = listOf(
        "-Xmx1024m",
        "-Xms512m",
        "-XX:MaxMetaspaceSize=256m",
        "-XX:+UseG1GC",
        "-XX:+HeapDumpOnOutOfMemoryError"
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
