plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:comment:domain"))
    implementation(project(":modules:content:comment:application"))
    implementation(project(":libs:adapter:web"))

    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.spring.boot.security)

    testImplementation(libs.bundles.kotlin.test)
}
