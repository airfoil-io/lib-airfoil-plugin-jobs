package io.airfoil.plugins.jobs.config

import io.airfoil.common.extension.boolValueOrDefault
import io.airfoil.common.extension.stringListOrEmpty
import io.ktor.server.config.*

class BackgroundJobsConfiguration {
    var startImmediately: Boolean = DEFAULT_START_IMMEDIATELY
    var disabledJobs: List<String> = emptyList()

    companion object {
        const val CONFIG_KEY = "jobs"

        const val DEFAULT_START_IMMEDIATELY = true

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): BackgroundJobsConfiguration = config.config(configKey).let { cfg ->
            BackgroundJobsConfiguration().also {
                it.startImmediately = cfg.boolValueOrDefault("startImmediately", DEFAULT_START_IMMEDIATELY)
                it.disabledJobs = cfg.stringListOrEmpty("disabledJobs")
            }
        }
    }
}
