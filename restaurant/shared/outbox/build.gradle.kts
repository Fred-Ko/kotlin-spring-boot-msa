import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management") // Version managed by parent
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    kotlin("kapt")
    // id("org.jlleitschuh.gradle.ktlint")
    // id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1" // Avro plugin - Rule 106/109 위반으로 제거
}

group = "com.restaurant.shared"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    // Confluent repository for Kafka Avro Serializer etc.
    maven { url = uri("https://packages.confluent.io/maven/") }
}

// Define versions in one place
val avroVersion = "1.11.3"
val kafkaAvroSerializerVersion = "7.5.3" // Check for the latest compatible version
val mapstructVersion = "1.5.5.Final"
val springBootVersion = "3.3.2" // 버전 수정

dependencies {
    // Import Spring Boot BOM
    implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // Project Dependencies

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.kafka:spring-kafka:$springBootVersion") // 버전 수정 반영
    implementation("org.springframework.boot:spring-boot-starter-validation") // Optional, as per instructions

    // Kafka & Avro
    implementation("org.apache.avro:avro:$avroVersion")
    implementation("io.confluent:kafka-avro-serializer:$kafkaAvroSerializerVersion")
    implementation("io.confluent:kafka-schema-registry-client:$kafkaAvroSerializerVersion")

    // MapStruct (Optional - for DTO mapping, e.g., DomainEvent <-> Avro)
    // implementation("org.mapstruct:mapstruct:$mapstructVersion")
    // kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // Database (Test scope)
    testRuntimeOnly("com.h2database:h2")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    // Add Kotest dependencies if needed
    // testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    // testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    // testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
    // dependsOn(tasks.withType<GenerateAvroJavaTask>()) // Avro plugin 제거로 주석 처리
    // source(tasks.withType<GenerateAvroJavaTask>().map { it.outputs }) // Avro plugin 제거로 주석 처리
}
// tasks.withType<GenerateAvroJavaTask> { // Avro plugin 제거로 주석 처리
//     isCreateSetters = false
//     fieldVisibility = "PRIVATE"
//     isCreateOptionalGetters = false
//     isGettersReturnOptional = false
// }

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configure Kotlin JPA plugin
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

// If using MapStruct, configure kapt
// kapt {
//    correctErrorTypes = true
// }
