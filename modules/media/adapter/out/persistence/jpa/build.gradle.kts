plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:adapter:persistence:jpa"))
    implementation(project(":modules:media:domain"))
    api(project(":modules:media:application"))

    implementation(libs.bundles.spring.boot.data)

    testImplementation(libs.bundles.kotlin.test)
    testImplementation(libs.spring.boot.starter.test)
}
