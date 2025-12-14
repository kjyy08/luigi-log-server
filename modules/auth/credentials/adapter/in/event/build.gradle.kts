plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:adapter:message:spring"))
    implementation(project(":modules:auth:credentials:application"))
    implementation(project(":modules:member:domain"))

    implementation(libs.spring.boot.starter.data.jpa)

}
