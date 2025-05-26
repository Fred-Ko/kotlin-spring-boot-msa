plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0" // For JPA Entities
    kotlin("plugin.allopen") version "2.1.0" // For JPA Entities
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

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

    implementation("org.springframework.boot:spring-boot-starter-web:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-security:3.5.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    
    // KotlinLogging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    
    // SpringDoc OpenAPI for Swagger UI - Spring Boot 3.2.5 호환 버전으로 업데이트
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6") {
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
        exclude(group = "org.webjars", module = "swagger-ui")
    }
    // 최신 호환 버전으로 명시적 지정
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.31")
    implementation("org.webjars:swagger-ui:5.22.0")
    
    runtimeOnly("org.postgresql:postgresql:42.7.5")
    runtimeOnly("com.h2database:h2:2.3.232")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("org.springframework.kafka:spring-kafka-test:4.0.0-M2")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.testcontainers:testcontainers:1.21.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
    testImplementation("org.testcontainers:kafka:1.21.0")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.confluent:kafka-avro-serializer:7.5.0") // Confluent Kafka version should match your Kafka broker version
    // Avro4k dependencies for Kotlin serialization with Avro
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:2.3.0") // Latest version of avro4k
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
