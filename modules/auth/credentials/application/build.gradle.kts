plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    api(project(":modules:auth:credentials:domain"))
    api(project(":modules:member:application"))
    compileOnly(project(":modules:member:domain"))

    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.bundles.kotlin.test)
}
