plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Common modules
    implementation(project(":libs:common-domain"))
    implementation(project(":libs:common-infrastructure"))
    implementation(project(":libs:common-web"))
    
    // Service modules
    implementation(project(":service:user:core"))
    implementation(project(":service:user:adapter-in"))
    implementation(project(":service:user:adapter-out"))
    
    implementation(project(":service:content:core"))
    implementation(project(":service:content:adapter-in"))
    implementation(project(":service:content:adapter-out"))
    
    implementation(project(":service:search:core"))
    implementation(project(":service:search:adapter-in"))
    implementation(project(":service:search:adapter-out"))
    
    implementation(project(":service:analytics:core"))
    implementation(project(":service:analytics:adapter-in"))
    implementation(project(":service:analytics:adapter-out"))
    
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2") // For testing
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}