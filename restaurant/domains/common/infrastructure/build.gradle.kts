plugins {
    kotlin("jvm")
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

dependencies {
    api(project(":domains:common:domain")) // BaseEntity 등에서 공통 도메인 요소 참조 가능성
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // BaseEntity @MappedSuperclass 등
    implementation("org.apache.avro:avro:1.11.3") // Envelope 스키마 생성용
}

// Avro 플러그인 설정 (Envelope 스키마 생성용)
avro {
    stringType.set("String")
    fieldVisibility.set("PRIVATE")
    // createSetters, outputDir 옵션은 지원되지 않으므로 제거
}
// 기본 avro 플러그인 출력 디렉토리 사용 (srcDirs 자동 설정)

