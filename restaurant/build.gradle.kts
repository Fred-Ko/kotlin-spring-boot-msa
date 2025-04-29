import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20" apply false
    id("org.springframework.boot") version "3.3.5" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}

apply(plugin = "io.spring.dependency-management")


configure<DependencyManagementExtension> {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.testcontainers:testcontainers-bom:1.19.7")
        mavenBom("io.kotest:kotest-bom:5.8.1")
    }
}

tasks.register("printSubprojects") {
    doLast {
        println("Root project: ${'$'}{project.name}")
        subprojects.forEach {
            println("  Subproject: ${'$'}{it.name} (Path: ${'$'}{it.path})")
        }
    }
}


allprojects {

    plugins.apply("org.jetbrains.kotlin.jvm")

    if (project != rootProject) {
        group = "com.restaurant"
        version = "0.0.1-SNAPSHOT"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}



subprojects.forEach { subproject ->
    subproject.plugins.apply("org.jetbrains.kotlin.jvm")
    subproject.plugins.apply("java")
    subproject.extensions.configure(org.gradle.api.plugins.JavaPluginExtension::class.java) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
    subproject.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
        kotlinOptions {
            jvmTarget = "21"
            freeCompilerArgs += "-Xjsr305=strict"
        }
    }
    subproject.dependencies {
        add("api", "org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
        add("api", "org.jetbrains.kotlin:kotlin-reflect:1.9.24")
        add("api", "org.slf4j:slf4j-api:2.0.13")
        add("implementation", "io.github.microutils:kotlin-logging-jvm:3.0.5")
        add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
        add("implementation", "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")

        add("testImplementation", "io.kotest:kotest-runner-junit5:5.8.1")
        add("testImplementation", "io.kotest:kotest-assertions-core:5.8.1")
        add("testImplementation", "io.mockk:mockk:1.14.0")
        add("testImplementation", "org.mockito.kotlin:mockito-kotlin:5.4.0")
        add("testImplementation", "org.assertj:assertj-core:3.25.3")
        add("testImplementation", "org.jetbrains.kotlin:kotlin-test:1.9.24")
        add("testImplementation", "org.springframework.boot:spring-boot-starter-test:3.4.5")
    }

    subproject.configurations.getByName("testImplementation").withDependencies {
        val dependencySet = this as DependencySet
        dependencySet.filterIsInstance<ExternalModuleDependency>().forEach { dependency ->
            if (dependency.group == "org.springframework.boot" && dependency.name == "spring-boot-starter-test") {
                dependency.exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
            }
        }
    }
}
