plugins {
    kotlin("jvm")

    
}

dependencies {
    api(project(":domains:user:domain"))
    implementation(project(":domains:common"))
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-avro-serializer:7.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
        implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.springframework:spring-context:6.1.6")
}
// Avro Kotlin 클래스 생성 설정 및 sourceSets 주석 필요시 추가
