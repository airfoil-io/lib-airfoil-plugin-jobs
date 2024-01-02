rootProject.name = "lib-airfoil-plugin-jobs"

pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
        maven("https://plugins.gradle.org/m2/")
	}
}

include(
	"lib"
)
