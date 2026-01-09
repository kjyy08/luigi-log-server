group = "cloud.luigi99"
description = "Luigi Blog Server"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<Jar> {
        archiveBaseName.set(project.path.replace(":", "-").substring(1))
    }
}
