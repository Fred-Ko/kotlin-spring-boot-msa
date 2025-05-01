plugins {
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") apply false
    kotlin("plugin.allopen") apply false
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    api(project(":domains:user:domain"))
    api(project(":domains:common:domain"))
api(project(":domains:common:infrastructure"))
    api(project(":independent:outbox"))
    implementation("org.springframework:spring-context:6.1.6")
    implementation("org.springframework:spring-tx:6.1.6")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")
    implementation("org.apache.avro:avro:1.11.3")
    implementation("io.confluent:kafka-avro-serializer:7.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-api:2.0.13")
}
avro {
    stringType.set("String")
    fieldVisibility.set("PRIVATE")
}
sourceSets["main"].java.srcDir("$buildDir/generated-main-avro-java")
