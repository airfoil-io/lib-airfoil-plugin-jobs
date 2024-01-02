package io.airfoil.plugins.jobs

import io.airfoil.common.extension.boolValueOrDefault
import io.airfoil.common.extension.withLogMetadata
import io.airfoil.common.plugin.createKtorApplicationPlugin
import io.airfoil.plugins.jobs.config.BackgroundJobsConfiguration
import io.ktor.server.application.*
import io.ktor.util.*
import mu.KotlinLogging

private const val TAG = "BackgroundJobsControllerPlugin"
private val log = KotlinLogging.logger(TAG)

val backgroundJobsConfigAttrKey = AttributeKey<BackgroundJobsConfiguration>("BackgroundJobsConfiguration")

val Application.backgroundJobsConfig: BackgroundJobsConfiguration
    get() = attributes[backgroundJobsConfigAttrKey]

fun Application.backgroundJobsConfig(backgroundJobsConfig: BackgroundJobsConfiguration) {
    attributes.put(backgroundJobsConfigAttrKey, backgroundJobsConfig)
}

fun Application.loadBackgroundJobsConfiguration(): BackgroundJobsConfiguration =
    BackgroundJobsConfiguration.load(environment.config).also {
        backgroundJobsConfig(it)
    }

private val backgroundJobsControllerAttrKey = AttributeKey<BackgroundJobsController>("BackgroundJobsController")

val Application.backgroundJobsController: BackgroundJobsController
    get() = attributes[backgroundJobsControllerAttrKey]

fun Application.backgroundJobsController(backgroundJobsController: BackgroundJobsController) {
    attributes.put(backgroundJobsControllerAttrKey, backgroundJobsController)
}

fun Application.configureBackgroundJobs() {
    loadBackgroundJobsConfiguration()

    install(BackgroundJobsControllerPlugin) {
        startImmediately = backgroundJobsConfig.startImmediately
    }
}

private val BackgroundJobsControllerPlugin  = createKtorApplicationPlugin(
    name = "Background Jobs Controller Plugin",
    createConfiguration = ::BackgroundJobsConfiguration,
) {
    log.info {
        "Configuring background jobs controller".withLogMetadata(
            "startImmediately" to pluginConfig.startImmediately
        )
    }

    application.backgroundJobsController(
        BackgroundJobsController(
            config = pluginConfig,
        )
    )

    application.backgroundJobsController
}
