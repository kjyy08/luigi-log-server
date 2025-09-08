plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":libs:common-infrastructure"))
    implementation(project(":service:search:core"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Future: Elasticsearch dependencies will be added here
    // implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    
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