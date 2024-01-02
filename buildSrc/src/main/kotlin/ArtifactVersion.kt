import org.gradle.api.Project

const val SNAPSHOT_VERSION = "1.0-snapshot"

fun Project.airfoilArtifactVersion(): String = this.findProperty("artifactVersion")?.toString()
    ?: SNAPSHOT_VERSION
