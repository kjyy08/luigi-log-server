// JPA/Persistence 관련 버전 정의
val hibernateVersion = "6.6.3.Final"
val h2Version = "2.3.232"

plugins {
    id("spring-boot")
    kotlin("plugin.jpa")
}

dependencies {
    // 자동으로 common-infrastructure 의존성 제공
    implementation(project(Modules.Libs.commonInfrastructure.path))

    // JPA & Hibernate (모든 persistence 모듈에 자동 제공)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-core:$hibernateVersion")

    // Database
    runtimeOnly("com.h2database:h2:$h2Version")

    // 테스트 의존성 자동 제공
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}