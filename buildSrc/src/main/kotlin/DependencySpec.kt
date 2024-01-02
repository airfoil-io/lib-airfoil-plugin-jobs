import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.plugin.use.PluginDependenciesSpec

data class DependencySpec(
    val name: String,
    val version: String = "",
    val snapshotVersion: String? = null,
    val isChanging: Boolean = false,
    val exclude: List<String> = emptyList()
) {
    fun plugin(scope: PluginDependenciesSpec, useSnapshots: Boolean = false) {
        scope.apply {
            println("plugin [${toDependencyNotation(useSnapshots)}]")
            id(name).version(calculateVersion(useSnapshots))
        }
    }

    fun classpath(scope: ScriptHandlerScope, useSnapshots: Boolean = false) {
        val spec = this
        with(scope) {
            dependencies {
                spec.toDependencyNotation(useSnapshots).also { dep ->
                    println("classpath [$dep]")
                    classpath(dep)
                }
            }
        }
    }

    fun implementation(handler: DependencyHandlerScope, useSnapshots: Boolean = false) {
        val spec = this
        with(handler) {
            spec.toDependencyNotation(useSnapshots).also { dep ->
                println("implementation [$dep]")
                "implementation".invoke(dep) {
                    isChanging = spec.isChanging
                    spec.exclude.forEach { excludeDependencyNotation ->
                        val (group, module) = excludeDependencyNotation.split(":", limit = 2)
                        this.exclude(group = group, module = module)
                    }
                }
            }
        }
    }

    fun integrationTestImplementation(handler: DependencyHandlerScope, useSnapshots: Boolean = false) {
        val spec = this
        with(handler) {
            spec.toDependencyNotation(useSnapshots).also { dep ->
                println("integrationTestImplementation [$dep]")
                "integrationTestImplementation".invoke(dep) {
                    isChanging = spec.isChanging
                    spec.exclude.forEach { excludeDependencyNotation ->
                        val (group, module) = excludeDependencyNotation.split(":", limit = 2)
                        this.exclude(group = group, module = module)
                    }
                }
            }
        }
    }

    fun runtimeOnly(handler: DependencyHandlerScope, useSnapshots: Boolean = false) {
        val spec = this
        with(handler) {
            spec.toDependencyNotation(useSnapshots).also { dep ->
                println("runtimeOnly [$dep]")
                "runtimeOnly".invoke(dep) {
                    isChanging = spec.isChanging
                    spec.exclude.forEach { excludeDependencyNotation ->
                        val (group, module) = excludeDependencyNotation.split(":", limit = 2)
                        this.exclude(group = group, module = module)
                    }
                }
            }
        }
    }

    fun testImplementation(handler: DependencyHandlerScope, useSnapshots: Boolean = false) {
        val spec = this
        with(handler) {
            spec.toDependencyNotation(useSnapshots).also { dep ->
                println("testImplementation [$dep]")
                "testImplementation".invoke(dep) {
                    isChanging = spec.isChanging
                    spec.exclude.forEach { excludeDependencyNotation ->
                        val (group, module) = excludeDependencyNotation.split(":", limit = 2)
                        this.exclude(group = group, module = module)
                    }
                }
            }
        }
    }

    fun testRuntimeOnly(handler: DependencyHandlerScope, useSnapshots: Boolean = false) {
        val spec = this
        with(handler) {
            spec.toDependencyNotation(useSnapshots).also { dep ->
                println("testRuntimeOnly [$dep]")
                "testRuntimeOnly".invoke(dep) {
                    isChanging = spec.isChanging
                    spec.exclude.forEach { excludeDependencyNotation ->
                        val (group, module) = excludeDependencyNotation.split(":", limit = 2)
                        this.exclude(group = group, module = module)
                    }
                }
            }
        }
    }

    fun calculateVersion(useSnapshots: Boolean = false): String? = when (useSnapshots) {
        true -> snapshotVersion.takeIf { !it.isNullOrEmpty() } ?: version.takeIf { it.isNotEmpty() }
        false -> version.takeIf { it.isNotEmpty() }
    }

    fun toDependencyNotation(useSnapshots: Boolean = false): String =
        listOfNotNull(
            name,
            calculateVersion(useSnapshots)
        ).joinToString(":")
}
