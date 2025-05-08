/**
 * settings.gradle.kts for the project.
 *
 * Configures plugin repositories and project structure for Gradle.
 *
 * @author junoko
 */

rootProject.name = "restaurant"

include(
    "apps:user-app",
    "domains:common:domain",
    "domains:common:application",
    "domains:common:infrastructure",
    "domains:common:presentation",
    "domains:user:domain",
    "domains:user:application",
    "domains:user:infrastructure:persistence",
    "domains:user:infrastructure:messaging",
    "domains:user:presentation",
    "independent:outbox"
)

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
        // maven { url = uri("https://jitpack.io") } // jitpack 의존성 없을 경우 삭제
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.springframework.boot") {
                useVersion("3.3.4")
            }
            if (requested.id.id == "io.spring.dependency-management") {
                useVersion("1.1.6")
            }
            if (requested.id.id == "org.jetbrains.kotlin.jvm" || 
                requested.id.id == "org.jetbrains.kotlin.plugin.spring" ||
                requested.id.id == "org.jetbrains.kotlin.plugin.jpa" ||
                requested.id.id == "org.jetbrains.kotlin.plugin.allopen") {
                useVersion("2.0.20")
            }
            if (requested.id.id == "org.jlleitschuh.gradle.ktlint") {
                useVersion("12.1.1")
            }
            if (requested.id.id == "com.github.davidmc24.gradle.plugin.avro") {
                useVersion("1.9.1")
            }
        }
    }
}
