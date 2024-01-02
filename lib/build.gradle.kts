import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val projectGroupId = "io.airfoil"
val projectArtifactId = "lib-jobs-plugin"
val projectRepoName = "lib-airfoil-plugin-jobs"

val gprUser = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
val gprKey = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_KEY")

plugins {
	`java-library`
    `maven-publish`
    kotlin("jvm") version Versions.Kotlin
    id("kotlinx-serialization")
}

group = projectGroupId

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()

    listOf(
        Repositories.Airfoil.LibCommon,
    ).forEach { repo ->
        repo.maven(this, gprUser, gprKey)
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/airfoil-io/$projectRepoName")
            credentials {
                username = gprUser
                password = gprKey
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            groupId = projectGroupId
            artifactId = projectArtifactId
            version = airfoilArtifactVersion()

            from(components["java"])
        }
    }
}

val useSnapshots = if (project.properties["useSnapshots"]?.toString() == null) { false } else { true }

dependencies {
    listOf(
        Dependencies.Airfoil.LibCommon,
        Dependencies.Ktor.KotlinxSerialization.Json,
        Dependencies.Ktor.Server.Core,
        Dependencies.MicroUtils.KotlinLogging,
    ).forEach { dep ->
        dep.implementation(this, useSnapshots)
    }

    listOf(
        Dependencies.Kotest.AssertionsCore,
        Dependencies.Kotest.FrameworkDataset,
        Dependencies.Kotest.FrameworkEngine,
        Dependencies.Kotest.Property,
        Dependencies.Kotest.RunnerJunit5,
	).forEach { dep ->
        dep.testImplementation(this, useSnapshots)
    }
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
    testLogging {
        events = setOf(
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED,
        )
    }
    reports.html.required.set(true)
}

tasks.withType<TestReport> {
    logging.captureStandardOutput(LogLevel.DEBUG)
    logging.captureStandardError(LogLevel.DEBUG)
}

tasks.withType<AbstractArchiveTask> {
    setProperty("archiveBaseName", projectArtifactId)
}

tasks.named("build") {
    if (airfoilArtifactVersion() == SNAPSHOT_VERSION) {
        finalizedBy("publishToMavenLocal")
    }
}
