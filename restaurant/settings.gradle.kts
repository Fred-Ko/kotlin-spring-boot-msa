/**
 * settings.gradle.kts for the project.
 *
 * Configures plugin repositories and project structure for Gradle.
 *
 * @author junoko
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
        maven { url = uri("https://jitpack.io") }
        flatDir { dirs("libs") }
    }
}

rootProject.name = "restaurant"

include(
    ":domains:common:domain",
    ":domains:common:presentation",
    ":domains:common:infrastructure",
    ":domains:user:presentation",
    ":domains:user:application",
    ":domains:user:domain",
    ":domains:user:infrastructure:persistence",
    ":domains:user:infrastructure:messaging",
    ":independent:outbox",
    ":apps:user-app",
)

fun includeGradleBuilds(vararg paths: String) {
    paths.forEach { path ->
        val moduleName = path.substring(1).replace("/", ":")
        includeBuild(path) {
            dependencySubstitution {
                substitute(module(moduleName)).using(project(":"))
            }
        }
    }
}

fun isCiServer(): Boolean {
    return System.getenv("CI") != null
}

fun shouldIncludeBuild(modulePath: String): Boolean {
    val ci = isCiServer()
    val requestedPath = System.getProperty("includeBuild")

    return when {
        !ci && requestedPath == null -> true // Local build, no specific request: include all
        !ci && requestedPath != null -> modulePath == requestedPath // Local build, specific request: include only requested
        else -> false // CI build: never include builds
    }
}
