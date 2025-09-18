import org.gradle.plugin.use.PluginDependenciesSpec

fun PluginDependenciesSpec.kotlinBase() = id("kotlin-base")
fun PluginDependenciesSpec.test() = id("test")
fun PluginDependenciesSpec.springBoot() = id("spring-boot")
fun PluginDependenciesSpec.domain() = id("domain")
fun PluginDependenciesSpec.web() = id("web")
fun PluginDependenciesSpec.persistence() = id("persistence")
fun PluginDependenciesSpec.application() = id("application")
fun PluginDependenciesSpec.koverReporting() = id("kover-reporting")