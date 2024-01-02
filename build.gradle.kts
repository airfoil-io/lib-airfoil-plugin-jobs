plugins {
    kotlin("jvm") version Versions.Kotlin apply false
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.Kotlin
    id("java")
    `maven-publish`
}

allprojects {
    val project = this
    group = "io.airfoil.plugin.jobs"
    version = airfoilArtifactVersion()

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply {
        Plugins.Kotlin.addTo(this)
        Plugins.Idea.addTo(this)
        Plugins.Kotlinx.Serialization.addTo(this)
        plugin("java")
        plugin("maven-publish")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
