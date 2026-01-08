plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    api(project(":modules:content:post:domain"))

    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.bundles.kotlin.test)
}
