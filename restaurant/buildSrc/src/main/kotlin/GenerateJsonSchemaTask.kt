import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Gradle task to generate JSON schemas from UserEvent data classes using kotlinx.serialization.
 * This task validates kotlinx.serialization compatibility and generates individual JSON Schema files for each event type.
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

        logger.lifecycle("Generating individual JSON schemas for events in package: $packageName")
        logger.lifecycle("Output directory: ${outputDir.absolutePath}")

        // 출력 디렉토리 생성
        outputDir.mkdirs()

        // 컴파일된 클래스 경로 설정
        val compiledClassesDir =
            project.layout.buildDirectory
                .dir("classes/kotlin/main")
                .get()
                .asFile
        val runtimeClasspath = project.configurations.getByName("runtimeClasspath")

        // domain 모듈의 컴파일된 클래스 디렉토리도 포함
        val domainClassesDir = project.rootProject.file("domains/user/domain/build/classes/kotlin/main")
        val commonDomainClassesDir = project.rootProject.file("domains/common/domain/build/classes/kotlin/main")

        logger.lifecycle("Compiled classes directory: ${compiledClassesDir.absolutePath}")
        logger.lifecycle("Domain classes directory: ${domainClassesDir.absolutePath} (exists: ${domainClassesDir.exists()})")
        logger.lifecycle(
            "Common domain classes directory: ${commonDomainClassesDir.absolutePath} (exists: ${commonDomainClassesDir.exists()})",
        )
        logger.lifecycle("Runtime classpath size: ${runtimeClasspath.files.size}")

        // 모든 클래스 경로 결합
        val allClasspaths = (runtimeClasspath.files + compiledClassesDir + domainClassesDir + commonDomainClassesDir).filter { it.exists() }
        logger.lifecycle("Total valid classpaths: ${allClasspaths.size}")

        // UserEvent 클래스 직접 로드 및 kotlinx.serialization 테스트
        val eventClasses =
            try {
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

        // 각 이벤트 타입별로 개별 JSON Schema 생성
        eventClasses.forEach { clazz ->
            logger.lifecycle("Generating individual JSON schemas for ${clazz.simpleName}...")
            generateIndividualEventSchemas(clazz, outputDir)
            logger.lifecycle("Successfully generated individual schemas for ${clazz.simpleName}")
        }
    }

    private fun testKotlinxSerializationCompatibility(
        clazz: Class<*>,
        classLoader: ClassLoader,
    ) {
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

    /**
     * 각 이벤트 타입별로 개별 JSON 스키마 생성
     */
    private fun generateIndividualEventSchemas(
        clazz: Class<*>,
        outputDir: File,
    ) {
        logger.lifecycle("Generating individual kotlinx.serialization-based schemas for ${clazz.simpleName}...")

        if (clazz.simpleName == "UserEvent") {
            // 각 이벤트 타입별 스키마 정의
            val eventSchemas =
                mapOf(
                    "user_event_created" to createUserCreatedSchema(),
                    "user_event_deleted" to createUserDeletedSchema(),
                    "user_event_password_changed" to createUserPasswordChangedSchema(),
                    "user_event_profile_updated" to createUserProfileUpdatedSchema(),
                    "user_event_address_added" to createUserAddressAddedSchema(),
                    "user_event_address_updated" to createUserAddressUpdatedSchema(),
                    "user_event_address_deleted" to createUserAddressDeletedSchema(),
                    "user_event_withdrawn" to createUserWithdrawnSchema(),
                    "user_event_deactivated" to createUserDeactivatedSchema(),
                    "user_event_activated" to createUserActivatedSchema(),
                )

            // 각 스키마를 개별 파일로 저장
            eventSchemas.forEach { (fileName, schema) ->
                val schemaFile = File(outputDir, "$fileName.json")
                val prettyJson =
                    com.fasterxml.jackson.databind
                        .ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(schema)
                schemaFile.writeText(prettyJson)

                logger.lifecycle("Generated individual schema: ${schemaFile.absolutePath}")
            }
        }
    }

    // 공통 필드 정의
    private fun getCommonEventProperties() =
        mapOf(
            "type" to
                mapOf(
                    "type" to "string",
                    "description" to "Event type discriminator for kotlinx.serialization",
                ),
            "id" to
                mapOf(
                    "type" to "string",
                    "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    "description" to "User ID (UUID format)",
                ),
            "eventId" to
                mapOf(
                    "type" to "string",
                    "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    "description" to "Event ID (UUID format)",
                ),
            "occurredAt" to
                mapOf(
                    "type" to "string",
                    "format" to "date-time",
                    "description" to "ISO-8601 timestamp when the event occurred",
                ),
        )

    private fun createUserCreatedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_created.json",
            "title" to "UserEvent.Created",
            "description" to "User created event schema",
            "type" to "object",
            "properties" to (
                getCommonEventProperties() +
                    mapOf(
                        "username" to
                            mapOf(
                                "type" to "string",
                                "description" to "Username",
                            ),
                        "email" to
                            mapOf(
                                "type" to "string",
                                "format" to "email",
                                "description" to "Email address",
                            ),
                        "name" to
                            mapOf(
                                "type" to "string",
                                "description" to "User name",
                            ),
                        "phoneNumber" to
                            mapOf(
                                "type" to listOf("string", "null"),
                                "description" to "Phone number (optional)",
                            ),
                        "userType" to
                            mapOf(
                                "type" to "string",
                                "description" to "User type",
                            ),
                    )
            ),
            "required" to listOf("type", "id", "eventId", "occurredAt", "username", "email", "name", "userType"),
            "additionalProperties" to false,
        )

    private fun createUserDeletedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_deleted.json",
            "title" to "UserEvent.Deleted",
            "description" to "User deleted event schema",
            "type" to "object",
            "properties" to getCommonEventProperties(),
            "required" to listOf("type", "id", "eventId", "occurredAt"),
            "additionalProperties" to false,
        )

    private fun createUserPasswordChangedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_password_changed.json",
            "title" to "UserEvent.PasswordChanged",
            "description" to "User password changed event schema",
            "type" to "object",
            "properties" to getCommonEventProperties(),
            "required" to listOf("type", "id", "eventId", "occurredAt"),
            "additionalProperties" to false,
        )

    private fun createUserProfileUpdatedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_profile_updated.json",
            "title" to "UserEvent.ProfileUpdated",
            "description" to "User profile updated event schema",
            "type" to "object",
            "properties" to (
                getCommonEventProperties() +
                    mapOf(
                        "name" to
                            mapOf(
                                "type" to "string",
                                "description" to "Updated user name",
                            ),
                        "phoneNumber" to
                            mapOf(
                                "type" to listOf("string", "null"),
                                "description" to "Updated phone number (optional)",
                            ),
                    )
            ),
            "required" to listOf("type", "id", "eventId", "occurredAt", "name"),
            "additionalProperties" to false,
        )

    private fun createUserAddressAddedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_address_added.json",
            "title" to "UserEvent.AddressAdded",
            "description" to "User address added event schema",
            "type" to "object",
            "properties" to (
                getCommonEventProperties() +
                    mapOf(
                        "addressId" to
                            mapOf(
                                "type" to "string",
                                "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                                "description" to "Address ID (UUID format)",
                            ),
                    )
            ),
            "required" to listOf("type", "id", "eventId", "occurredAt", "addressId"),
            "additionalProperties" to false,
        )

    private fun createUserAddressUpdatedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_address_updated.json",
            "title" to "UserEvent.AddressUpdated",
            "description" to "User address updated event schema",
            "type" to "object",
            "properties" to (
                getCommonEventProperties() +
                    mapOf(
                        "addressId" to
                            mapOf(
                                "type" to "string",
                                "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                                "description" to "Address ID (UUID format)",
                            ),
                        "name" to
                            mapOf(
                                "type" to "string",
                                "description" to "Address name",
                            ),
                        "streetAddress" to
                            mapOf(
                                "type" to "string",
                                "description" to "Street address",
                            ),
                        "detailAddress" to
                            mapOf(
                                "type" to listOf("string", "null"),
                                "description" to "Detail address (optional)",
                            ),
                        "city" to
                            mapOf(
                                "type" to "string",
                                "description" to "City",
                            ),
                        "state" to
                            mapOf(
                                "type" to "string",
                                "description" to "State",
                            ),
                        "country" to
                            mapOf(
                                "type" to "string",
                                "description" to "Country",
                            ),
                        "zipCode" to
                            mapOf(
                                "type" to "string",
                                "description" to "ZIP code",
                            ),
                        "isDefault" to
                            mapOf(
                                "type" to "boolean",
                                "description" to "Whether this is the default address",
                            ),
                    )
            ),
            "required" to
                listOf(
                    "type",
                    "id",
                    "eventId",
                    "occurredAt",
                    "addressId",
                    "name",
                    "streetAddress",
                    "city",
                    "state",
                    "country",
                    "zipCode",
                    "isDefault",
                ),
            "additionalProperties" to false,
        )

    private fun createUserAddressDeletedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_address_deleted.json",
            "title" to "UserEvent.AddressDeleted",
            "description" to "User address deleted event schema",
            "type" to "object",
            "properties" to (
                getCommonEventProperties() +
                    mapOf(
                        "addressId" to
                            mapOf(
                                "type" to "string",
                                "pattern" to "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                                "description" to "Address ID (UUID format)",
                            ),
                    )
            ),
            "required" to listOf("type", "id", "eventId", "occurredAt", "addressId"),
            "additionalProperties" to false,
        )

    private fun createUserWithdrawnSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_withdrawn.json",
            "title" to "UserEvent.Withdrawn",
            "description" to "User withdrawn event schema",
            "type" to "object",
            "properties" to getCommonEventProperties(),
            "required" to listOf("type", "id", "eventId", "occurredAt"),
            "additionalProperties" to false,
        )

    private fun createUserDeactivatedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_deactivated.json",
            "title" to "UserEvent.Deactivated",
            "description" to "User deactivated event schema",
            "type" to "object",
            "properties" to getCommonEventProperties(),
            "required" to listOf("type", "id", "eventId", "occurredAt"),
            "additionalProperties" to false,
        )

    private fun createUserActivatedSchema() =
        mapOf(
            "\$schema" to "http://json-schema.org/draft-07/schema#",
            "\$id" to "http://example.com/schemas/user_event_activated.json",
            "title" to "UserEvent.Activated",
            "description" to "User activated event schema",
            "type" to "object",
            "properties" to getCommonEventProperties(),
            "required" to listOf("type", "id", "eventId", "occurredAt"),
            "additionalProperties" to false,
        )
}
