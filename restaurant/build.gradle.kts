import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.ktlint) apply false
}

apply(plugin = "io.spring.dependency-management")
// apply(plugin = "org.jlleitschuh.gradle.ktlint")

// Root dependencies - comment out ktlint dependency
/* // Comment out ktlint dependency block
dependencies {
    add("ktlint", "com.pinterest.ktlint:ktlint-cli:12.1.1")
}
*/

// Resolve ktlint version string at root level - Comment out
// val ktlintVersion = libs.versions.ktlintGradle.get()

configure<DependencyManagementExtension> {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
        mavenBom(libs.testcontainers.bom.get().toString())
        mavenBom(libs.kotest.bom.get().toString())
    }
}

tasks.register("printSubprojects") {
    doLast {
        println("Root project: ${project.name}")
        subprojects.forEach {
            println("  Subproject: ${it.name} (Path: ${it.path})")
        }
    }
}

// Comment out ktlint tasks
/*
tasks.register("ktlintCheckAll") {
    group = "verification"
    description = "Runs ktlint checks on all projects."
    dependsOn(allprojects.mapNotNull { p -> p.tasks.findByName("ktlintCheck")?.path })
}

tasks.register("ktlintFormatAll") {
    group = "formatting"
    description = "Runs ktlint formatting on all projects."
    dependsOn(allprojects.mapNotNull { p -> p.tasks.findByName("ktlintFormat")?.path })
}
*/

// Configure all projects (including root) individually
allprojects {
    // Apply common plugins to all projects
    plugins.apply("org.jetbrains.kotlin.jvm")
    plugins.apply("java-library")
    // plugins.apply("org.jlleitschuh.gradle.ktlint") // Comment out ktlint plugin apply

    // Apply group and version only to subprojects
    if (project != rootProject) {
    group = "com.restaurant"
    version = "0.0.1-SNAPSHOT"
    }

    // Comment out ktlint configuration
    /*
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(ktlintVersion) // ktlintVersion is commented out
        debug.set(true)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        filter {
            // EXPLICITLY REMOVE CONTENT INSIDE filter {}
        }
    }
    */

    // Configure tasks for all projects
    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    // Default task configurations for subprojects (can be overridden)
    if (project != rootProject) {
    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        enabled = false
    }
    tasks.withType<Jar> {
        enabled = true
        }
    }
}

// Configure common dependencies for subprojects individually AFTER applying plugins
subprojects.forEach { subproject ->
    subproject.dependencies {
        add("api", libs.kotlin.stdlib)
        add("api", libs.kotlin.reflect)
        add("api", libs.slf4j.api)
        add("implementation", libs.kotlin.logging.jvm)
        add("implementation", libs.jackson.module.kotlin)
        add("implementation", libs.jackson.datatype.jsr310)

        add("testImplementation", libs.kotest.runner.junit5)
        add("testImplementation", libs.kotest.assertions.core)
        add("testImplementation", libs.mockk)
        add("testImplementation", libs.mockito.kotlin)
        add("testImplementation", libs.assertj.core)
        add("testImplementation", libs.kotlin.test)
        add("testImplementation", libs.spring.boot.starter.test)
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
