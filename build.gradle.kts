allprojects {
    group = "cloud.luigi99"
    description = "Luigi Log Server"

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<Jar> {
        archiveBaseName.set(project.path.replace(":", "-").substring(1))
    }
}
