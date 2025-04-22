plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

avro {
    isCreateSetters.set(false)
    fieldVisibility.set("PRIVATE")
}

dependencies {
    // 기본 라이브러리 의존성
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0") // OptimisticLockException 사용 위해 추가

    // Spring Framework 핵심 의존성
    implementation("org.springframework.boot:spring-boot-starter:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2") // ProblemDetail 사용을 위해 필요
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.2") // RepresentationModel 사용을 위해 필요
    
    // Avro and Schema Registry
    implementation("org.apache.avro:avro:1.12.0")
    implementation("io.confluent:kafka-avro-serializer:7.6.0")
    implementation("io.confluent:kafka-schema-registry-client:7.6.0")

    // Outbox 모듈 의존성 (GlobalExceptionHandler에서 OutboxException 처리를 위해 필요)
    implementation(project(":independent:outbox:infrastructure"))

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}
