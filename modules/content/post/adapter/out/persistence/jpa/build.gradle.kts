plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:post:domain"))
    implementation(project(":modules:content:post:application"))
    implementation(project(":libs:adapter:persistence:jpa"))
    implementation(project(":libs:adapter:message:spring"))

    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.bundles.kotlin.test)
}
