package io.airfoil.plugins.jobs

import io.airfoil.common.extension.withLogMetadata
import io.airfoil.common.plugin.KtorApplicationPlugin
import io.airfoil.plugins.jobs.config.BackgroundJobsConfiguration
import io.ktor.server.application.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private const val TAG = "BackgroundJobsController"
private val log = KotlinLogging.logger(TAG)

class BackgroundJobsController(
    private val config: BackgroundJobsConfiguration,
) : KtorApplicationPlugin {

    private val jobContexts = mutableMapOf<String, JobContext>()

    fun startJobs() {
        log.info { "Starting background jobs".withLogMetadata("count" to jobContexts.size) }
        jobContexts.forEach { (_, context) ->
            context.start()
        }
    }

    fun stopJobs() {
        log.info { "Stopping background jobs".withLogMetadata("count" to jobContexts.size) }
        jobContexts.forEach { (_, context) ->
            runBlocking { context.stop() }
        }
    }

    fun active(): List<JobContext> = jobContexts.values.filter { it.isActive }

    fun cancelled(): List<JobContext> = jobContexts.values.filter { it.isCancelled }

    fun completed(): List<JobContext> = jobContexts.values.filter { it.isComplete }

    fun addJob(job: JobContext, autoStart: Boolean = true) {
        if (!config.disabledJobs.contains(job.name)) {
            jobContexts[job.name] = job
            if (autoStart) {
                job.start()
            }
            log.info { "Added background job".withLogMetadata("name" to job.name, "started" to autoStart) }
        } else {
            log.warn { "Skipping disabled background job".withLogMetadata("name" to job.name) }
        }
    }

    override fun onApplicationStarted(application: Application) {
        if (config.startImmediately) {
            startJobs()
        }
    }

    override fun onApplicationStopped(application: Application) {
        stopJobs()
    }

}
