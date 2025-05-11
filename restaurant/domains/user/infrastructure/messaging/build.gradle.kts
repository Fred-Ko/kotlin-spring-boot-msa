plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:infrastructure"))
    implementation(project(":independent:outbox"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.apache.avro:avro:1.11.3")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

avro {
    stringType.set("String")
    outputCharacterEncoding.set("UTF-8")
    isCreateSetters.set(true)
    isCreateOptionalGetters.set(false)
    isGettersReturnOptional.set(false)
    fieldVisibility.set("PRIVATE")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceSets {
        main {
            java {
                srcDir("build/generated-main-avro-java")
            }
        }
    }
}

kotlin {
    jvmToolchain(21)
    sourceSets.main {
        kotlin.srcDirs(
            "src/main/kotlin",
            "build/generated-main-avro-java"
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask>("generateAvroJava") {
    source("src/main/resources/avro")
    setOutputDir(file("build/generated-main-avro-java"))
}

tasks.named("compileKotlin") {
    dependsOn("generateAvroJava")
}
