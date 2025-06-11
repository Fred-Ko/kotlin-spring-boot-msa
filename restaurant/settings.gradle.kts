import org.gradle.api.initialization.resolve.RepositoriesMode

/**
 * settings.gradle.kts for the project.
 *
 * Configures plugin repositories and project structure for Gradle.
 *
 * @author junoko
 */

rootProject.name = "restaurant"

include(
    "domains:account:presentation"
)

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories { 
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }  // Add JetBrains repo for Kotlin
        maven { url = uri("https://packages.confluent.io/maven/") }
        // Spring Milestones and Snapshots for Spring Cloud compatibility if needed in the future
        // maven { url = uri("https://repo.spring.io/milestone") }
    }
}

// Common modules
include(":domains:common:domain")
include(":domains:common:application")
include(":domains:common:infrastructure")
include(":domains:common:presentation")

// User modules
include(":domains:user:domain")
include(":domains:user:application")
include(":domains:user:infrastructure")
include(":domains:user:presentation")

// Account modules
include(":domains:account:domain")
include(":domains:account:application")
include(":domains:account:infrastructure")
include(":domains:account:presentation")

// Independent modules
include(":independent:outbox")

// Application modules
include(":apps:user-app")
include(":apps:account-app")
