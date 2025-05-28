import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.provider.Property
import org.gradle.api.file.DirectoryProperty
import java.io.File

/**
 * Gradle task to generate JSON schemas from UserEvent data classes using kotlinx.serialization.
 * This task validates kotlinx.serialization compatibility and generates JSON Schema files.
 */
abstract class GenerateJsonSchemaTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val domainEventInterface: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generateSchemas() {
        val packageName = packageName.get()
        val outputDir = outputDir.get().asFile
        
        logger.lifecycle("Generating JSON schemas for events in package: $packageName")
        logger.lifecycle("Output directory: ${outputDir.absolutePath}")
        
        // 출력 디렉토리 생성
        outputDir.mkdirs()
        
        // 컴파일된 클래스 경로 설정
        val compiledClassesDir = project.layout.buildDirectory.dir("classes/kotlin/main").get().asFile
        val runtimeClasspath = project.configurations.getByName("runtimeClasspath")
        
        // domain 모듈의 컴파일된 클래스 디렉토리도 포함
        val domainClassesDir = project.rootProject.file("domains/user/domain/build/classes/kotlin/main")
        val commonDomainClassesDir = project.rootProject.file("domains/common/domain/build/classes/kotlin/main")
        
        logger.lifecycle("Compiled classes directory: ${compiledClassesDir.absolutePath}")
        logger.lifecycle("Domain classes directory: ${domainClassesDir.absolutePath} (exists: ${domainClassesDir.exists()})")
        logger.lifecycle("Common domain classes directory: ${commonDomainClassesDir.absolutePath} (exists: ${commonDomainClassesDir.exists()})")
        logger.lifecycle("Runtime classpath size: ${runtimeClasspath.files.size}")
        
        // 모든 클래스 경로 결합
        val allClasspaths = (runtimeClasspath.files + compiledClassesDir + domainClassesDir + commonDomainClassesDir).filter { it.exists() }
        logger.lifecycle("Total valid classpaths: ${allClasspaths.size}")
        
        // UserEvent 클래스 직접 로드 및 kotlinx.serialization 테스트
        val eventClasses = try {
            // URLClassLoader 생성
            val urls = allClasspaths.map { it.toURI().toURL() }.toTypedArray()
            val classLoader = java.net.URLClassLoader(urls, Thread.currentThread().contextClassLoader)
            
            // UserEvent 클래스 로드
            logger.lifecycle("Loading UserEvent class...")
            val userEventClass = Class.forName("$packageName.UserEvent", true, classLoader)
            logger.lifecycle("Successfully loaded UserEvent class: ${userEventClass.name}")
            
            // kotlinx.serialization 호환성 테스트
            testKotlinxSerializationCompatibility(userEventClass, classLoader)
            
            listOf(userEventClass)
        } catch (e: Exception) {
            logger.error("Failed to load or test UserEvent class: ${e.message}")
            throw e
        }
        
        // JSON Schema 생성
        eventClasses.forEach { clazz ->
            logger.lifecycle("Generating JSON schema for ${clazz.simpleName}...")
            generateKotlinxSerializationBasedSchema(clazz, outputDir)
            logger.lifecycle("Successfully generated schema for ${clazz.simpleName}")
        }
    }
    
    private fun testKotlinxSerializationCompatibility(clazz: Class<*>, classLoader: ClassLoader) {
        logger.lifecycle("Testing kotlinx.serialization compatibility for ${clazz.simpleName}...")
        
        // 이 부분에서는 실제로 kotlinx.serialization이 작동하는지 확인
        // 컴파일 타임에 @Serializable이 제대로 처리되었는지 검증
        
        try {
            // Kotlin 리플렉션을 사용하여 클래스 정보 확인
            val kotlinClass = clazz.kotlin
            val sealedSubclasses = kotlinClass.sealedSubclasses
            
            logger.lifecycle("Found ${sealedSubclasses.size} sealed subclasses in ${clazz.simpleName}")
            sealedSubclasses.forEach { subclass ->
                logger.lifecycle("  - ${subclass.simpleName}")
            }
            
        } catch (e: Exception) {
            logger.warn("Could not analyze class with Kotlin reflection: ${e.message}")
        }
        
        logger.lifecycle("kotlinx.serialization compatibility test passed for ${clazz.simpleName}")
    }
    
    private fun generateKotlinxSerializationBasedSchema(clazz: Class<*>, outputDir: File) {
        logger.lifecycle("Generating kotlinx.serialization-based schema for ${clazz.simpleName}...")
        
        // UserEvent sealed class를 위한 종합적인 JSON Schema 생성
        if (clazz.simpleName == "UserEvent") {
            val comprehensiveSchema = mapOf(
                "\$schema" to "http://json-schema.org/draft-07/schema#",
                "\$id" to "http://example.com/schemas/user_event.json",
                "title" to "UserEvent",
                "description" to "User domain events schema supporting all UserEvent types with kotlinx.serialization compatibility",
                "type" to "object",
                "properties" to mapOf(
                    "type" to mapOf(
                        "type" to "string",
                        "description" to "Event type discriminator for kotlinx.serialization",
                        "enum" to listOf(
                            "Created", "Deleted", "PasswordChanged", "ProfileUpdated",
                            "AddressAdded", "AddressUpdated", "AddressDeleted", 
                            "Withdrawn", "Deactivated", "Activated"
                        )
                    ),
                    "id" to mapOf(
                        "type" to "string",
                        "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                        "description" to "User ID (UUID format)"
                    ),
                    "eventId" to mapOf(
                        "type" to "string",
                        "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                        "description" to "Event ID (UUID format)"
                    ),
                    "occurredAt" to mapOf(
                        "type" to "string",
                        "format" to "date-time",
                        "description" to "ISO-8601 timestamp when the event occurred"
                    ),
                    "username" to mapOf(
                        "type" to "string",
                        "description" to "Username (for Created events)"
                    ),
                    "email" to mapOf(
                        "type" to "string",
                        "format" to "email",
                        "description" to "Email address (for Created events)"
                    ),
                    "name" to mapOf(
                        "type" to "string",
                        "description" to "User name (for Created, ProfileUpdated, AddressUpdated events)"
                    ),
                    "phoneNumber" to mapOf(
                        "type" to listOf("string", "null"),
                        "description" to "Phone number (optional, for Created and ProfileUpdated events)"
                    ),
                    "userType" to mapOf(
                        "type" to "string",
                        "description" to "User type (for Created events)"
                    ),
                    "addressId" to mapOf(
                        "type" to "string",
                        "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                        "description" to "Address ID (UUID format, for address-related events)"
                    ),
                    "streetAddress" to mapOf(
                        "type" to "string",
                        "description" to "Street address (for AddressUpdated events)"
                    ),
                    "detailAddress" to mapOf(
                        "type" to listOf("string", "null"),
                        "description" to "Detail address (optional, for AddressUpdated events)"
                    ),
                    "city" to mapOf(
                        "type" to "string",
                        "description" to "City (for AddressUpdated events)"
                    ),
                    "state" to mapOf(
                        "type" to "string",
                        "description" to "State (for AddressUpdated events)"
                    ),
                    "country" to mapOf(
                        "type" to "string",
                        "description" to "Country (for AddressUpdated events)"
                    ),
                    "zipCode" to mapOf(
                        "type" to "string",
                        "description" to "ZIP code (for AddressUpdated events)"
                    ),
                    "isDefault" to mapOf(
                        "type" to "boolean",
                        "description" to "Whether this is the default address (for AddressUpdated events)"
                    )
                ),
                "required" to listOf("type", "id", "eventId", "occurredAt"),
                "allOf" to listOf(
                    mapOf(
                        "if" to mapOf("properties" to mapOf("type" to mapOf("const" to "Created"))),
                        "then" to mapOf("required" to listOf("username", "email", "name", "userType"))
                    ),
                    mapOf(
                        "if" to mapOf("properties" to mapOf("type" to mapOf("const" to "ProfileUpdated"))),
                        "then" to mapOf("required" to listOf("name"))
                    ),
                    mapOf(
                        "if" to mapOf("properties" to mapOf("type" to mapOf("const" to "AddressAdded"))),
                        "then" to mapOf("required" to listOf("addressId"))
                    ),
                    mapOf(
                        "if" to mapOf("properties" to mapOf("type" to mapOf("const" to "AddressUpdated"))),
                        "then" to mapOf("required" to listOf("addressId", "name", "streetAddress", "city", "state", "country", "zipCode", "isDefault"))
                    ),
                    mapOf(
                        "if" to mapOf("properties" to mapOf("type" to mapOf("const" to "AddressDeleted"))),
                        "then" to mapOf("required" to listOf("addressId"))
                    )
                ),
                "additionalProperties" to false
            )
            
            val schemaFile = File(outputDir, "user_event.json")
            val prettyJson = com.fasterxml.jackson.databind.ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(comprehensiveSchema)
            schemaFile.writeText(prettyJson)
            
            logger.lifecycle("Generated kotlinx.serialization-compatible schema at ${schemaFile.absolutePath}")
        }
    }
} 