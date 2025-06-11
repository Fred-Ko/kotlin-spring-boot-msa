plugins {
    `java-library`
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.github.imflog.kafka-schema-registry-gradle-plugin") version "2.3.2"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-security:3.5.0")
    implementation("org.springframework.kafka:spring-kafka:3.3.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    
    // Confluent Schema Registry
    implementation("io.confluent:kafka-avro-serializer:7.7.0")
    implementation("io.confluent:kafka-schema-registry-client:7.7.0")
    
    // Project dependencies
    api(project(":domains:account:domain"))
    api(project(":domains:account:application"))
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:infrastructure"))
    implementation(project(":independent:outbox"))
    
    // Database
    runtimeOnly("org.postgresql:postgresql:42.7.4")
    implementation("org.flywaydb:flyway-core:11.8.2")
    implementation("org.flywaydb:flyway-database-postgresql:11.8.2")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.3.6")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:kafka:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

// Schema Registry 설정 - 스키마 다운로드
schemaRegistry {
    url.set("http://localhost:8081")

    download {
        val env = System.getenv("ENV") ?: "dev"
        val team = "restaurant"
        val userDomain = "user"
        val dataType = "event"
        val version = "v1"

        // UserEvent.Created
        subject("$env.$team.$userDomain.$dataType.created.$version-value", "UserEventCreated.json")
        // UserEvent.Deactivated  
        subject("$env.$team.$userDomain.$dataType.deactivated.$version-value", "UserEventDeactivated.json")
    }
}

// 스키마 파일을 올바른 위치로 이동하는 태스크
val moveSchemaFiles by tasks.registering {
    description = "Move downloaded schema files to correct location"
    group = "build"
    
    dependsOn("downloadSchemasTask")
    
    doLast {
        val targetDir = file("src/main/resources/schemas/user")
        targetDir.mkdirs()
        
        // 루트 디렉토리에서 스키마 파일들을 찾아서 이동
        val rootDir = project.rootDir
        
        // UserEventCreated 스키마 파일 이동
        val userCreatedDir = File(rootDir, "UserEventCreated.json")
        if (userCreatedDir.exists() && userCreatedDir.isDirectory()) {
            val schemaFile = File(userCreatedDir, "dev.restaurant.user.event.created.v1-value.json")
            if (schemaFile.exists()) {
                val targetFile = File(targetDir, "UserEventCreated.json")
                schemaFile.copyTo(targetFile, overwrite = true)
                logger.info("Moved UserEventCreated schema to ${targetFile.absolutePath}")
            }
            // 원본 디렉토리 삭제
            userCreatedDir.deleteRecursively()
        }
        
        // UserEventDeactivated 스키마 파일 이동
        val userDeactivatedDir = File(rootDir, "UserEventDeactivated.json")
        if (userDeactivatedDir.exists() && userDeactivatedDir.isDirectory()) {
            val schemaFile = File(userDeactivatedDir, "dev.restaurant.user.event.deactivated.v1-value.json")
            if (schemaFile.exists()) {
                val targetFile = File(targetDir, "UserEventDeactivated.json")
                schemaFile.copyTo(targetFile, overwrite = true)
                logger.info("Moved UserEventDeactivated schema to ${targetFile.absolutePath}")
            }
            // 원본 디렉토리 삭제
            userDeactivatedDir.deleteRecursively()
        }
        
        logger.info("Schema files moved to: ${targetDir.absolutePath}")
    }
}

// 커스텀 태스크: JSON 스키마로부터 Kotlin 데이터 클래스 생성
val generateDataClasses by tasks.registering {
    description = "Generate Kotlin data classes from JSON schemas"
    group = "build"
    
    // account 모듈 내부의 스키마 파일 경로
    val schemaDir = file("src/main/resources/schemas/user")
    val outputDir = file("src/main/kotlin/com/restaurant/account/infrastructure/messaging/event")
    
    inputs.dir(schemaDir).optional(true)
    outputs.dir(outputDir)
    
    dependsOn(moveSchemaFiles)
    
    doLast {
        // 출력 디렉토리 생성
        outputDir.mkdirs()
        
        // 스키마 파일이 존재하는지 확인
        if (!schemaDir.exists()) {
            logger.warn("Schema directory does not exist: ${schemaDir.absolutePath}")
            return@doLast
        }
        
        // account 모듈 내부의 스키마 파일 경로
        val userCreatedSchemaFile = file("src/main/resources/schemas/user/UserEventCreated.json")
        val userDeactivatedSchemaFile = file("src/main/resources/schemas/user/UserEventDeactivated.json")
        
        // UserEventCreated 데이터 클래스 생성
        if (userCreatedSchemaFile.exists()) {
            val userEventCreatedFile = File(outputDir, "UserEventCreated.kt")
            userEventCreatedFile.writeText("""
package com.restaurant.account.infrastructure.messaging.event

import kotlinx.serialization.Serializable

@Serializable
data class UserEventCreated(
    val type: String,
    val id: String,
    val eventId: String,
    val occurredAt: String,
    val username: String,
    val email: String,
    val name: String,
    val phoneNumber: String? = null,
    val userType: String
)
            """.trimIndent())
            logger.info("Generated UserEventCreated.kt from schema")
        } else {
            logger.warn("UserEventCreated schema file not found: ${userCreatedSchemaFile.absolutePath}")
        }
        
        // UserEventDeactivated 데이터 클래스 생성
        if (userDeactivatedSchemaFile.exists()) {
            val userEventDeactivatedFile = File(outputDir, "UserEventDeactivated.kt")
            userEventDeactivatedFile.writeText("""
package com.restaurant.account.infrastructure.messaging.event

import kotlinx.serialization.Serializable

@Serializable
data class UserEventDeactivated(
    val type: String,
    val id: String,
    val eventId: String,
    val occurredAt: String
)
            """.trimIndent())
            logger.info("Generated UserEventDeactivated.kt from schema")
        } else {
            logger.warn("UserEventDeactivated schema file not found: ${userDeactivatedSchemaFile.absolutePath}")
        }
        
        logger.info("Generated data classes in: ${outputDir.absolutePath}")
    }
}

// 소스 세트에 생성된 코드 디렉토리 추가
sourceSets {
    main {
        kotlin {
            srcDir("src/main/kotlin")
        }
    }
}

// 태스크 의존성 설정: 다운로드 → 데이터 클래스 생성 → 컴파일 순서
tasks.named("compileKotlin") {
    dependsOn(generateDataClasses)
}

tasks.named("processResources") {
    dependsOn("downloadSchemasTask")
} 