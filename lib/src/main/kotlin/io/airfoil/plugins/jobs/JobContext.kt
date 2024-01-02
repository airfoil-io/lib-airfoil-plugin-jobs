package io.airfoil.plugins.jobs

import io.airfoil.common.extension.withLogMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "JobContext"
private val log = KotlinLogging.logger(TAG)

class JobContext(
    val name: String,
    private val jobId: JobId,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val restartBehavior: RestartBehavior = RestartBehavior(),
    private val work: suspend () -> Unit,
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = dispatcher + exceptionHandler

    private val exceptionHandler = CoroutineExceptionHandler { _, error ->
        if (error is CancellationException) {
            log.warn { "job cancelled".withJobContextMetadata(error) }
            return@CoroutineExceptionHandler
        }

        log.error(error) { "job failed".withJobContextMetadata(error) }
        if (restartBehavior.restartOnError) {
            restartFromError()
        }
    }

    private var restarts = 0
    private var job: Job? = null

    val isCancelled: Boolean
        get() = job?.isCancelled == true

    val isComplete: Boolean
        get() = job?.isCompleted == true

    fun executeBlocking() {
        runBlocking {
            work()
        }
    }

    fun start() {
        runBlocking { stop() }
        doStart()
    }

    suspend fun stop() {
        job?.run {
            cancelAndJoin()
            log.info { "job stopped".withJobContextMetadata() }
        }
        job = null
    }

    private fun doStart(delayStart: Duration? = null) {
        job = launch(coroutineContext) {
            delayStart?.let { delay(it) }
            work()
        }
        log.info { "job started".withJobContextMetadata() }
    }

    private fun restartFromError() {
        if (restartBehavior.maxRestarts > 0 && restarts++ >= restartBehavior.maxRestarts) {
            log.warn { "not restarting, maximum restarts reached".withJobContextMetadata() }
            return
        }
        log.warn { "restarting from error".withJobContextMetadata() }
        restarts++
        doStart()
    }

    private fun String.withJobContextMetadata(error: Throwable? = null): String {
        val meta = mutableListOf<Pair<String, Any>>("name" to name, "jobId" to jobId)
        error?.message?.let { meta += "errorMessage" to it }
        return withLogMetadata(meta)
    }

    data class RestartBehavior(
        val restartOnError: Boolean = true,
        val restartDelay: Duration = 1.seconds,
        val maxRestarts: Int = -1,
    ) {
        companion object {
            val None = RestartBehavior(restartOnError = false, maxRestarts = 0)
        }
    }
}
