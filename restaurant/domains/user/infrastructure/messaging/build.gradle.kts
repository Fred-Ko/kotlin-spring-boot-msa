plugins {
    kotlin("jvm")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
    `java-library`
}

dependencies {
    api(project(":domains:user:domain"))
    implementation(project(":domains:common"))
    implementation("org.apache.avro:avro:1.12.2")
    implementation("io.confluent:kafka-avro-serializer:7.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:1.9.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.springframework:spring-context:6.1.6")
}
// Avro Kotlin 클래스 생성 설정 및 sourceSets 주석 필요시 추가
