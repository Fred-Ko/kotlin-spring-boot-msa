plugins {
    kotlin("jvm")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    api(project(":domains:user:domain"))
    implementation(project(":domains:common"))
    implementation(project(":independent:outbox"))
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-avro-serializer:7.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.springframework:spring-context:6.1.6")
}
avro {
    stringType.set("String")
    fieldVisibility.set("PRIVATE")
}
sourceSets["main"].java.srcDir("$buildDir/generated-main-avro-java")
