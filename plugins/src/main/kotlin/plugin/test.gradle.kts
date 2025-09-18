// 테스트 관련 버전 정의
val kotestVersion = "5.8.0"
val kotlinVersion = "1.9.25"
val junitPlatformVersion = "1.11.3"

plugins {
    java
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}