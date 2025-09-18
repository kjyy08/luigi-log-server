// 도메인 관련 버전 정의 (현재는 기본 도메인 모델만 사용)
val slf4jVersion = "2.0.16"

plugins {
    id("kotlin-base")
    id("test")
}

dependencies {
    // 자동으로 common-domain 의존성 제공
    implementation(project(Modules.Libs.commonDomain.path))

    // 로깅 (도메인 이벤트 로깅용)
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
}