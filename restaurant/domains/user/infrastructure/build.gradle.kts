plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    // Temporarily disable Avro plugin to fix circular dependency
    // id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:common")) // EventEnvelope 등 공통 Avro 클래스 사용
    implementation(project(":independent:outbox:api")) // OutboxException 사용을 위한 의존성 추가

    implementation("org.springframework.boot:spring-boot-starter:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.mapstruct:mapstruct:1.6.3")

    // Avro & Kafka (직렬화/역직렬화만 필요, 플러그인/코드 생성 불필요)
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

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/java")
        kotlin.srcDirs("src/main/kotlin")
        resources {
            srcDirs("src/main/resources")
            exclude("avro/") // Exclude the avro directory
        }
        // Avro generated sources will be added automatically
    }
}

// Avro plugin configuration (Kotlin code generation)
// avro {
//    isCreateSetters.set(false)
//    fieldVisibility.set("PRIVATE")
//    // Default outputDir works for Kotlin if you use the right package in .avsc
//    // Generated sources: build/generated-src/avro/main/kotlin
// }
