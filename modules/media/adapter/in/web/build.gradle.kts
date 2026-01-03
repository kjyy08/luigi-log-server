plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:adapter:web"))
    implementation(project(":modules:media:domain"))
    implementation(project(":modules:media:application"))

    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.spring.boot.security)

    testImplementation(libs.bundles.kotlin.test)
}
