plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:common"))
    implementation(project(":independent:outbox:application")) // OutboxEventRepository 사용을 위한 의존성 추가

    implementation("org.springframework.boot:spring-boot-starter:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.mapstruct:mapstruct:1.6.3")

    // Avro & Kafka
    implementation("org.apache.avro:avro:1.11.3")
    implementation("io.confluent:kafka-avro-serializer:7.5.3")
    implementation("io.confluent:kafka-schema-registry-client:7.5.3")
    implementation("org.apache.kafka:kafka-clients")

    // MapStruct 어노테이션 프로세서 - Kotlin에서는 kapt 사용
    kapt("org.mapstruct:mapstruct-processor:1.6.3")

    // H2 데이터베이스
    runtimeOnly("com.h2database:h2:2.3.232")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Avro 플러그인 설정
avro {
    isCreateSetters.set(false)
    fieldVisibility.set("PRIVATE")
    isCreateOptionalGetters.set(false)
    isGettersReturnOptional.set(false)
    outputCharacterEncoding.set("UTF-8")
    stringType.set("String")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    source(tasks.withType<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask>().map { it.outputs })
}

tasks.named("compileKotlin") {
    dependsOn("generateAvroJava")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}
