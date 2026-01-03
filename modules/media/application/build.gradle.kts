plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    api(project(":modules:media:domain"))

    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.bundles.kotlin.test)
}
