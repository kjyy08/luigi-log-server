plugins {
    springBootConventions
}

dependencies {
    implementation(project(":modules:content:post:domain"))
    implementation(project(":modules:content:post:application"))
    implementation(project(":libs:adapter:persistence:redis"))

    implementation(libs.spring.boot.starter.data.redis)

    testImplementation(libs.bundles.kotlin.test)
}
