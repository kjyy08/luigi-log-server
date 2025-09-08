plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":libs:common-infrastructure"))
    implementation(project(":service:ai:core"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Future Phase 2: Vector database and AI service integrations
    // implementation("org.springframework.ai:spring-ai-qdrant-store-spring-boot-starter")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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