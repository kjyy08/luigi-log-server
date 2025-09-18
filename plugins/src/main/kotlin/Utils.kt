import org.gradle.api.Project

fun Project.createJarName(): String {
    return if (parent != null) {
        "${parent!!.name}-${name}"
    } else {
        name
    }
}