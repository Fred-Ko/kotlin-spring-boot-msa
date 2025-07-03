import java.net.URLClassLoader

plugins {
    `java-library`
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0" // For JPA Entities
    kotlin("plugin.allopen") version "2.1.0" // For JPA Entities
    kotlin("plugin.serialization") version "2.1.0" // For kotlinx.serialization
    id("com.github.imflog.kafka-schema-registry-gradle-plugin") version "2.3.2"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:infrastructure")) // For BaseEntity if needed
    implementation(project(":domains:payment:domain"))
    implementation(project(":domains:payment:application")) // For DTOs if any indirect use
    implementation(project(":independent:outbox")) // For OutboxMessageRepository interface

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    implementation("org.flywaydb:flyway-core:11.8.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.19.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7") // Logging

    // kotlinx.serialization 의존성 추가
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Kafka 관련
    implementation("org.springframework.kafka:spring-kafka:4.0.0-M2")
    implementation("org.apache.kafka:kafka-clients:3.9.0")
    implementation("io.confluent:kafka-schema-registry-client:7.8.0")
    implementation("io.confluent:kafka-json-schema-serializer:7.8.0")

    runtimeOnly("org.postgresql:postgresql:42.7.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.testcontainers:testcontainers:1.21.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xjsr305=strict",
                "-Xfriend-paths=${project(":domains:payment:domain").layout.buildDirectory.dir("classes/kotlin/main").get().asFile.absolutePath}"
            )
        )
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

// JSON Schema 생성 태스크 (Rule VII.1.3.2) - buildSrc의 GenerateJsonSchemaTask 사용
tasks.register<GenerateJsonSchemaTask>("generateJsonSchema") {
    group = "schema"
    description = "Generate JSON schemas from PaymentEvent data classes using Jackson JsonSchemaGenerator"
    dependsOn("compileKotlin")

    packageName.set("com.restaurant.payment.domain.event")
    domainEventInterface.set("com.restaurant.common.domain.event.DomainEvent")
    outputDir.set(file("src/main/resources/schemas"))
}

// Schema Registry 설정 (Rule VII.1.3.3) - 새로운 토픽 네이밍 정책 적용
schemaRegistry {
    url.set("http://localhost:8081") // 개발 환경용, 실제 환경에서는 ENV 변수 등으로 설정

    // === 자동화된 스키마 등록 ===
    val env = System.getenv("ENV") ?: "dev"
    val team = "payment-team"
    val domain = "payment"
    val dataType = "event"
    val version = "v1"

    fun File.toActionName(): String =
        this.nameWithoutExtension.removePrefix("payment_event_")
            .replace('_', '-')

    // schemas 디렉터리 내 *.json 파일을 모두 탐색하여 register/compatibility 설정 자동 추가
    val schemaDir = file("src/main/resources/schemas")
    val schemaFiles = schemaDir.listFiles { f -> f.extension == "json" }?.toList() ?: emptyList()

    register {
        schemaFiles.forEach { schemaFile ->
            val action = schemaFile.toActionName()
            val subjectName = "$env.$team.$domain.$dataType.$action.$version-value"
            subject(subjectName, schemaFile.absolutePath, "JSON")
        }
    }

    compatibility {
        schemaFiles.forEach { schemaFile ->
            val action = schemaFile.toActionName()
            val subjectName = "$env.$team.$domain.$dataType.$action.$version-value"
            subject(subjectName, schemaFile.absolutePath, "JSON")
        }
    }
}

// Schema Registry 태스크들이 generateJsonSchema 후에 실행되도록 설정 (Rule VII.1.3.3)
tasks.named("registerSchemasTask") {
    dependsOn("generateJsonSchema")
}

tasks.named("testSchemasTask") {
    dependsOn("generateJsonSchema")
}

// build 태스크가 스키마 생성을 포함하도록 설정 (Rule VII.1.3.2)
tasks.named("build") {
    dependsOn("generateJsonSchema")
}

// processResources가 generateJsonSchema 후에 실행되도록 설정
tasks.named("processResources") {
    dependsOn("generateJsonSchema")
} 