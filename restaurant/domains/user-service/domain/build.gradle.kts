dependencies {    
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(project(":support:common"))
    implementation(project(":libs:outbox"))
}