plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:post:domain"))
    implementation(project(":modules:content:post:application"))
    implementation(project(":libs:adapter:web"))

    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.spring.boot.security)

    testImplementation(libs.bundles.kotlin.test)
}
