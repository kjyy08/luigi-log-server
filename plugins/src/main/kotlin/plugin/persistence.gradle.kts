import gradle.kotlin.dsl.accessors._ffcf889acdea48f400c29ebf21a196f7.implementation
import gradle.kotlin.dsl.accessors._ffcf889acdea48f400c29ebf21a196f7.runtimeOnly
import gradle.kotlin.dsl.accessors._ffcf889acdea48f400c29ebf21a196f7.testImplementation

plugins {
    id("spring-boot")
    kotlin("plugin.jpa")
}

dependencies {
    // 자동으로 common-infrastructure 의존성 제공
    implementation(project(Modules.Libs.commonInfrastructure.path))

    // JPA & Hibernate (모든 persistence 모듈에 자동 제공)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")

    // 테스트 의존성 자동 제공
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}