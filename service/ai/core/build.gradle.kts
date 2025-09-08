plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":libs:common-domain"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Future Phase 2: AI dependencies will be added here
    // implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    
    testImplementation("org.junit.jupiter:junit-jupiter")
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