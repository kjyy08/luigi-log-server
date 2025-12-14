plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:auth:token:domain"))
    implementation(project(":modules:auth:token:application"))
    implementation(project(":modules:member:domain"))
    implementation(project(":libs:adapter:persistence:redis"))

    implementation(libs.spring.boot.starter.data.redis)

    testImplementation(libs.bundles.kotlin.test)
}
