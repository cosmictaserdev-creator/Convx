package com.convx.music.core.updater

import android.content.Context
import android.os.Build
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.convx.music.BuildConfig
import com.convx.music.core.UpdateNotificationHelper
import com.convx.music.core.updater.downloadmanager.UpdateDownloadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val source: GitHubReleaseSource,
    private val store: UpdateStore,
    private val installer: UpdateInstaller,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val initialized = AtomicBoolean()
    private val restored = CompletableDeferred<Unit>()
    private val lock = Mutex()
    private val mutableState = MutableStateFlow(UpdateUiState())
    val state = mutableState.asStateFlow()
    private val workManager by lazy { WorkManager.getInstance(context) }

    fun initialize() {
        if (!initialized.compareAndSet(false, true)) return
        scope.launch {
            val enabled = isGitHubBuild()
            if (!enabled) { mutableState.update { it.copy(enabled = false) }; restored.complete(Unit); return@launch }
            val cached = store.release
            if (cached != null && cached.asset.versionCode <= BuildConfig.VERSION_CODE) {
                store.release = null; store.readyPath = null; store.workId = null
                mutableState.update { it.copy(phase = UpdatePhase.INSTALLED) }
            } else if (cached != null) {
                val ready = readyFile() != null
                mutableState.update { it.copy(release = cached, phase = if (ready) UpdatePhase.READY else UpdatePhase.AVAILABLE,
                    floatingVisible = store.shouldPrompt(cached.id, System.currentTimeMillis())) }
            }
            val existingWork = workManager.getWorkInfosForUniqueWorkFlow(WORK_NAME).first()
            val activeWork = existingWork.firstOrNull { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.BLOCKED }
            if (activeWork != null && store.workId == activeWork.id.toString() && cached != null) {
                mutableState.update { it.copy(phase = if (activeWork.state == WorkInfo.State.RUNNING) UpdatePhase.DOWNLOADING else UpdatePhase.QUEUED) }
            }
            restored.complete(Unit)
            launch {
                workManager.getWorkInfosForUniqueWorkFlow(WORK_NAME).collect { infos ->
                    val info = infos.firstOrNull { it.id.toString() == store.workId } ?: return@collect
                    if (state.value.release == null || state.value.phase in setOf(UpdatePhase.INSTALLING, UpdatePhase.AWAITING_APPROVAL, UpdatePhase.INSTALLED)) return@collect
                    when (info.state) {
                        WorkInfo.State.RUNNING -> mutableState.update { it.copy(
                            phase = if (info.progress.getBoolean("verifying", false)) UpdatePhase.VERIFYING else UpdatePhase.DOWNLOADING,
                            progress = info.progress.getFloat("progress", -1f).takeIf { p -> p >= 0 }, error = null) }
                        WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> mutableState.update { it.copy(phase = UpdatePhase.QUEUED, error = null) }
                        WorkInfo.State.SUCCEEDED -> {
                            val path = info.outputData.getString("file_path")
                            store.readyPath = path
                            mutableState.update { it.copy(phase = if (readyFile() != null) UpdatePhase.READY else UpdatePhase.AVAILABLE, progress = null) }
                        }
                        WorkInfo.State.FAILED -> mutableState.update { it.copy(phase = UpdatePhase.FAILED, progress = null,
                            error = runCatching { UpdateFailure.valueOf(info.outputData.getString("error") ?: "NETWORK") }.getOrDefault(UpdateFailure.NETWORK)) }
                        WorkInfo.State.CANCELLED -> mutableState.update { it.copy(phase = UpdatePhase.AVAILABLE, progress = null, error = null) }
                    }
                }
            }
            launch {
                installer.outcome.collect { result ->
                    if (result != null) mutableState.update { it.copy(phase = result.phase, error = result.error) }
                }
            }
        }
    }

    fun check(manual: Boolean = false) = scope.launch {
        restored.await()
        lock.withLock {
            if (!isGitHubBuild()) { mutableState.update { it.copy(enabled = false) }; return@withLock }
            val now = System.currentTimeMillis()
            refreshPrompt(now)
            if (UpdatePolicy.isTransfer(state.value.phase)) return@withLock
            if (!manual && (!getAutoUpdateCheckSetting(context) || now - store.lastCheck < UpdatePolicy.CHECK_INTERVAL_MILLIS || now - store.lastAttempt < 60_000)) return@withLock
            store.lastAttempt = now
            mutableState.update { it.copy(phase = UpdatePhase.CHECKING, error = null) }
            try {
                val release = source.latest(getBetaUpdatesSetting(context))
                if (release?.id != store.release?.id) { store.readyPath = null; store.workId = null }
                store.release = release
                store.lastCheck = now
                saveLastCheckedTime(context, Instant.ofEpochMilli(now).toString())
                saveUpdateAvailableState(context, release != null)
                mutableState.value = UpdateUiState(release = release,
                    phase = if (release == null) UpdatePhase.IDLE else if (readyFile() != null) UpdatePhase.READY else UpdatePhase.AVAILABLE,
                    floatingVisible = release != null && store.shouldPrompt(release.id, now))
                if (release != null && getUpdateNotificationsSetting(context) && getLastNotifiedUpdateTag() != release.id && store.shouldPrompt(release.id, now)) {
                    UpdateNotificationHelper.showUpdateNotification(context, release.version)
                    saveLastNotifiedUpdateTag(release.id)
                }
            } catch (cancelled: CancellationException) { throw cancelled
            } catch (error: Exception) {
                mutableState.update { it.copy(phase = if (it.release == null) UpdatePhase.FAILED else if (readyFile() != null) UpdatePhase.READY else UpdatePhase.AVAILABLE,
                    error = (error as? UpdateException)?.failure ?: UpdateFailure.NETWORK) }
            }
        }
    }

    fun download() = scope.launch {
        restored.await()
        lock.withLock {
            val release = state.value.release ?: return@withLock
            if (!state.value.enabled || UpdatePolicy.isTransfer(state.value.phase)) return@withLock
            val request = OneTimeWorkRequestBuilder<UpdateDownloadWorker>()
                .setInputData(workDataOf("asset" to Json.encodeToString(release.asset)))
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS).build()
            store.workId = request.id.toString()
            store.readyPath = null
            mutableState.update { it.copy(phase = UpdatePhase.QUEUED, error = null, progress = null) }
            workManager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }

    fun cancel() = scope.launch { workManager.cancelUniqueWork(WORK_NAME) }
    fun clearDownloadedUpdate() = scope.launch {
        restored.await()
        lock.withLock {
            if (UpdatePolicy.isTransfer(state.value.phase)) return@withLock
            readyFile()?.delete()
            store.readyPath = null
            if (state.value.phase == UpdatePhase.READY) {
                mutableState.update { it.copy(phase = if (it.release == null) UpdatePhase.IDLE else UpdatePhase.AVAILABLE, progress = null, error = null) }
            }
        }
    }
    fun dismiss() = scope.launch {
        state.value.release?.let { store.dismiss(it.id) }
        mutableState.update { it.copy(floatingVisible = false) }
        UpdateNotificationHelper.cancelUpdateNotification(context)
    }
    fun snooze() = scope.launch {
        state.value.release?.let { store.snooze(it.id, System.currentTimeMillis()) }
        mutableState.update { it.copy(floatingVisible = false) }
        UpdateNotificationHelper.cancelUpdateNotification(context)
    }
    private fun refreshPrompt(now: Long) {
        state.value.release?.let { release -> mutableState.update { it.copy(floatingVisible = store.shouldPrompt(release.id, now)) } }
    }
    private fun readyFile(): File? = store.readyPath?.let(::File)?.takeIf {
        it.parentFile?.canonicalFile == File(context.noBackupFilesDir, "updates").canonicalFile && it.isFile
    }
    private fun isGitHubBuild(): Boolean {
        if (!BuildConfig.GITHUB_UPDATER_ENABLED || BuildConfig.DEBUG) return false
        val installedBy = if (Build.VERSION.SDK_INT >= 30) context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
            else @Suppress("DEPRECATION") context.packageManager.getInstallerPackageName(context.packageName)
        return installedBy !in setOf("com.android.vending", "org.fdroid.fdroid", "org.fdroid.basic")
    }
    companion object { const val WORK_NAME = "github_update_download" }
}
