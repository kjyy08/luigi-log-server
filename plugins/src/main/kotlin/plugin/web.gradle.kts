// Web 관련 버전 정의
val validationApiVersion = "3.1.0"
val servletApiVersion = "6.1.0"

plugins {
    id("spring-boot")
}

dependencies {
    // 자동으로 common-web 의존성 제공
    implementation(project(Modules.Libs.commonWeb.path))

    // Spring Web (모든 web 모듈에 자동 제공)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Web API
    implementation("jakarta.validation:jakarta.validation-api:$validationApiVersion")
    implementation("jakarta.servlet:jakarta.servlet-api:$servletApiVersion")

    // 테스트 의존성 자동 제공
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}