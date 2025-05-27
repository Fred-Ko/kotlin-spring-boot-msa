import java.net.URLClassLoader

plugins {
    `java-library`
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0" // For JPA Entities
    kotlin("plugin.allopen") version "2.1.0" // For JPA Entities
    id("com.github.imflog.kafka-schema-registry-gradle-plugin") version "2.3.2"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:infrastructure")) // For BaseEntity if needed
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:application")) // For DTOs if any indirect use
    implementation(project(":independent:outbox")) // For OutboxMessageRepository interface

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    implementation("org.flywaydb:flyway-core:11.8.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.19.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7") // Logging

    // Kafka 관련
    implementation("org.springframework.kafka:spring-kafka:4.0.0-M2")
    implementation("org.apache.kafka:kafka-clients:3.9.0")
    implementation("io.confluent:kafka-schema-registry-client:7.8.0")
    implementation("io.confluent:kafka-json-schema-serializer:7.8.0")

    runtimeOnly("org.postgresql:postgresql:42.7.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.testcontainers:testcontainers:1.21.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:postgresql:1.21.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

// JSON Schema 생성 태스크
tasks.register("generateJsonSchema") {
    group = "build"
    description = "Generate JSON schemas from DomainEvent data classes"
    
    dependsOn("compileKotlin")
    
    inputs.files(fileTree("src/main/kotlin") {
        include("**/event/**/*.kt")
    })
    outputs.dir("src/main/resources/schemas")
    
    doLast {
        val schemasDir = file("src/main/resources/schemas")
        schemasDir.mkdirs()
        
        // Jackson을 사용하여 UserEvent로부터 JSON 스키마 생성
        val classpathFiles = configurations.getByName("compileClasspath").files + 
                           configurations.getByName("runtimeClasspath").files +
                           sourceSets.main.get().output.classesDirs.files
        
        val classLoader = URLClassLoader(
            classpathFiles.map { it.toURI().toURL() }.toTypedArray(),
            Thread.currentThread().contextClassLoader
        )
        
        try {
            val objectMapper = classLoader.loadClass("com.fasterxml.jackson.databind.ObjectMapper")
                .getDeclaredConstructor().newInstance()
            
            // Kotlin 모듈 등록
            val kotlinModule = classLoader.loadClass("com.fasterxml.jackson.module.kotlin.KotlinModule\$Builder")
                .getDeclaredConstructor().newInstance()
                .also { it::class.java.getMethod("build").invoke(it) }
            
            val javaTimeModule = classLoader.loadClass("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
                .getDeclaredConstructor().newInstance()
            
            objectMapper::class.java.getMethod("registerModule", 
                classLoader.loadClass("com.fasterxml.jackson.databind.Module")).invoke(objectMapper, kotlinModule)
            objectMapper::class.java.getMethod("registerModule",
                classLoader.loadClass("com.fasterxml.jackson.databind.Module")).invoke(objectMapper, javaTimeModule)
            
            // JsonSchemaGenerator 생성
            val schemaGeneratorClass = classLoader.loadClass("com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator")
            val schemaGenerator = schemaGeneratorClass.getDeclaredConstructor(
                classLoader.loadClass("com.fasterxml.jackson.databind.ObjectMapper")
            ).newInstance(objectMapper)
            
            // UserEvent 클래스 로드
            val userEventClass = classLoader.loadClass("com.restaurant.user.domain.event.UserEvent")
            
            // JSON 스키마 생성
            val generateSchemaMethod = schemaGeneratorClass.getMethod("generateSchema", Class::class.java)
            val schema = generateSchemaMethod.invoke(schemaGenerator, userEventClass)
            
            // 스키마를 JSON 문자열로 변환
            val writeValueAsStringMethod = objectMapper::class.java.getMethod("writeValueAsString", Object::class.java)
            val schemaJson = writeValueAsStringMethod.invoke(objectMapper, schema) as String
            
            val schemaFile = file("src/main/resources/schemas/user_event.json")
            schemaFile.writeText(schemaJson)
            
            println("Generated JSON schema from UserEvent class: ${schemaFile.absolutePath}")
            
        } catch (e: Exception) {
            println("Failed to generate schema using Jackson, falling back to manual schema...")
            
            // Fallback: 수동으로 작성된 스키마 (실제 UserEvent 구조에 맞춰 수정)
            val userEventSchema = """
            {
              "${'$'}schema": "https://json-schema.org/draft/2020-12/schema",
              "${'$'}id": "https://restaurant.com/schemas/user-event.json",
              "title": "UserEvent",
              "description": "User domain events",
              "type": "object",
              "oneOf": [
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "username": { "type": "string" },
                    "email": { "type": "string" },
                    "name": { "type": "string" },
                    "phoneNumber": { "type": ["string", "null"] },
                    "userType": { "type": "string" },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "username", "email", "name", "userType", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.Created"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "name": { "type": "string" },
                    "phoneNumber": { "type": ["string", "null"] },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "name", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.ProfileUpdated"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.PasswordChanged"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "addressId": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "addressId", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.AddressAdded"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "addressId": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    },
                    "name": { "type": "string" },
                    "streetAddress": { "type": "string" },
                    "detailAddress": { "type": ["string", "null"] },
                    "city": { "type": "string" },
                    "state": { "type": "string" },
                    "country": { "type": "string" },
                    "zipCode": { "type": "string" },
                    "isDefault": { "type": "boolean" },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "addressId", "name", "streetAddress", "city", "state", "country", "zipCode", "isDefault", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.AddressUpdated"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "addressId": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "addressId", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.AddressDeleted"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.Withdrawn"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.Deactivated"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.Activated"
                },
                {
                  "type": "object",
                  "properties": {
                    "eventId": { "type": "string", "format": "uuid" },
                    "occurredAt": { "type": "string", "format": "date-time" },
                    "aggregateId": { "type": "string" },
                    "aggregateType": { "type": "string", "const": "User" },
                    "id": { 
                      "type": "object",
                      "properties": {
                        "value": { "type": "string", "format": "uuid" }
                      }
                    }
                  },
                  "required": ["eventId", "occurredAt", "aggregateId", "aggregateType", "id"],
                  "additionalProperties": false,
                  "title": "UserEvent.Deleted"
                }
              ]
            }
            """.trimIndent()
            
            val schemaFile = file("src/main/resources/schemas/user_event.json")
            schemaFile.writeText(userEventSchema)
            
            println("Generated fallback JSON schema: ${schemaFile.absolutePath}")
        } finally {
            classLoader.close()
        }
    }
}

tasks.named("processResources") {
    dependsOn("generateJsonSchema")
}

// Schema Registry 설정
schemaRegistry {
    url.set("http://localhost:8081") // 개발 환경용, 실제 환경에서는 환경변수로 설정
    
    register {
        subject("dev.user.domain-event.user.v1-value", "${projectDir}/src/main/resources/schemas/user_event.json", "JSON")
    }
    
    compatibility {
        subject("dev.user.domain-event.user.v1-value", "${projectDir}/src/main/resources/schemas/user_event.json", "JSON")
    }
}

// Schema Registry 태스크들이 generateJsonSchema 후에 실행되도록 설정
tasks.named("registerSchemasTask") {
    dependsOn("generateJsonSchema")
}

tasks.named("testSchemasTask") {
    dependsOn("generateJsonSchema")
}

// build 태스크가 스키마 생성을 포함하도록 설정
tasks.named("build") {
    dependsOn("generateJsonSchema")
} 