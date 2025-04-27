import java.net.URI
import java.io.File

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
    }
}

rootProject.name = "restaurant"

include(
    ":domains:common",
    ":domains:common:infrastructure",
    ":domains:user:presentation",
    ":domains:user:application",
    ":domains:user:domain",
    ":domains:user:infrastructure:messaging",
    ":domains:user:infrastructure:persistence",
    ":independent:outbox",
    ":independent:outbox:port",
    ":independent:outbox:infrastructure",
    ":config",
    ":apps:user-app"
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

// Recursively find all subprojects with build.gradle.kts excluding certain paths
file(".").walkTopDown().forEach { file ->
    if (file.name == "build.gradle.kts" && file.parentFile != settings.rootDir) {
        val modulePath = file.parentFile.relativeTo(settings.rootDir).path.replace(File.separatorChar, ':')
        val cleanModulePath = ":$modulePath"

        // Exclude paths containing build, out, .gradle, .idea, etc. and ensure it's a valid subproject path
        if (!file.parent.contains("build") &&
            !file.parent.contains("out") &&
            !file.parent.contains(".gradle") &&
            !file.parent.contains(".idea") &&
            !file.parent.contains(".run") &&
            cleanModulePath.startsWith(":") // Basic validation
           ) {
            println("Discovered module: $cleanModulePath")
            // You might need further logic here if you intended to use shouldIncludeBuild
        }
    }
}
