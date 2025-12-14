import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

val PluginDependenciesSpec.conventions: PluginDependencySpec
    get() = id("conventions")

val PluginDependenciesSpec.springLibraryConventions: PluginDependencySpec
    get() = id("spring-library-conventions")

val PluginDependenciesSpec.springBootConventions: PluginDependencySpec
    get() = id("spring-boot-conventions")

val PluginDependenciesSpec.koverReport: PluginDependencySpec
    get() = id("kover")