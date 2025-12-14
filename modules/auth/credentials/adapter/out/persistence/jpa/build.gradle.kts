plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:auth:credentials:domain"))
    implementation(project(":modules:auth:credentials:application"))
    implementation(project(":modules:member:domain"))
    implementation(project(":libs:adapter:persistence:jpa"))

    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.bundles.kotlin.test)
}
