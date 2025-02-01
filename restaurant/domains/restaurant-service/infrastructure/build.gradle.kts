dependencies {
    implementation(project(":domains:restaurant-service:domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.linecorp.kotlin-jdsl:querydsl-jpa-spring-boot-starter:4.1.0")
    runtimeOnly("com.h2database:h2") 
}
