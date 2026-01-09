plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:guestbook:domain"))
    implementation(project(":modules:content:guestbook:application"))
    implementation(project(":libs:adapter:web"))

    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.spring.boot.security)

    testImplementation(libs.bundles.kotlin.test)
}
