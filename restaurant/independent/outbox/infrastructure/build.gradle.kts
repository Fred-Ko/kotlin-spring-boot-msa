plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    kotlin("plugin.jpa") version "2.1.20"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    implementation(project(":independent:outbox:api"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.4")
    implementation("org.springframework.kafka:spring-kafka:3.1.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.20")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("io.confluent:kafka-avro-serializer:7.6.0")
    implementation("org.apache.avro:avro:1.12.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.4")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.1.2")
    testRuntimeOnly("com.h2database:h2:2.3.232")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

// kotlin("plugin.jpa")가 allopen 및 noArg 설정을 자동으로 처리하므로 제거

avro {
    isCreateSetters.set(false)
    fieldVisibility.set("PRIVATE")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}
