plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:guestbook:domain"))
    implementation(project(":modules:content:guestbook:application"))
    implementation(project(":libs:adapter:persistence:jpa"))
    implementation(project(":libs:adapter:message:spring"))

    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.bundles.kotlin.test)
}
