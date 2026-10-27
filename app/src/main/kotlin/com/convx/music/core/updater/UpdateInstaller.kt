package com.convx.music.core.updater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import androidx.core.app.NotificationCompat
import com.convx.music.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class InstallOutcome(val phase: UpdatePhase, val error: UpdateFailure? = null)

@Singleton
class UpdateInstaller @Inject constructor(
    @ApplicationContext private val context: Context,
    private val store: UpdateStore,
    private val verifier: ApkVerifier,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val lock = Mutex()
    private val mutableOutcome = MutableStateFlow<InstallOutcome?>(null)
    val outcome = mutableOutcome.asStateFlow()
    private val mutableConfirmation = MutableStateFlow<Intent?>(null)
    val confirmation = mutableConfirmation.asStateFlow()

    suspend fun install() = lock.withLock {
        val release = store.release ?: return@withLock
        val file = store.readyPath?.let(::File) ?: return@withLock
        val packages = context.packageManager.packageInstaller
        if (store.sessionId >= 0 && packages.getSessionInfo(store.sessionId)?.isCommitted == true) {
            mutableOutcome.value = InstallOutcome(UpdatePhase.AWAITING_APPROVAL)
            return@withLock
        }
        var sessionId = -1
        try {
            if (file.parentFile?.canonicalFile != File(context.noBackupFilesDir, "updates").canonicalFile) throw UpdateException(UpdateFailure.VERIFICATION)
            verifier.verify(file, release.asset)
            mutableOutcome.value = InstallOutcome(UpdatePhase.INSTALLING)
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL).apply {
                setAppPackageName(context.packageName)
                setSize(file.length())
                if (Build.VERSION.SDK_INT >= 31) setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_REQUIRED)
            }
            sessionId = packages.createSession(params)
            store.sessionId = sessionId
            store.installStatus = Int.MIN_VALUE
            packages.openSession(sessionId).use { session ->
                file.inputStream().use { input -> session.openWrite("base.apk", 0, file.length()).use { output ->
                    input.copyTo(output); session.fsync(output)
                } }
                val callback = Intent(context, UpdateInstallReceiver::class.java).setAction("${context.packageName}.UPDATE_RESULT")
                    .putExtra("expected_session", sessionId)
                val flags = PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
                session.commit(PendingIntent.getBroadcast(context, sessionId, callback, flags).intentSender)
            }
        } catch (cancelled: CancellationException) {
            if (sessionId >= 0) packages.abandonSession(sessionId)
            store.sessionId = -1
            throw cancelled
        } catch (error: Exception) {
            if (sessionId >= 0) runCatching { packages.abandonSession(sessionId) }
            store.sessionId = -1
            mutableOutcome.value = InstallOutcome(UpdatePhase.READY, (error as? UpdateException)?.failure ?: UpdateFailure.INSTALL_FAILED)
        }
    }

    fun onResult(intent: Intent, finished: () -> Unit) = scope.launch {
        try {
            if (intent.getIntExtra("expected_session", -2) != store.sessionId) return@launch
            val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
            store.installStatus = status
            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    @Suppress("DEPRECATION")
                    val confirmation = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT) ?: return@launch
                    mutableConfirmation.value = confirmation
                    mutableOutcome.value = InstallOutcome(UpdatePhase.AWAITING_APPROVAL)
                    notifyApproval(confirmation)
                }
                PackageInstaller.STATUS_SUCCESS -> {
                    mutableConfirmation.value = null
                    store.sessionId = -1
                    mutableOutcome.value = InstallOutcome(UpdatePhase.INSTALLED)
                }
                else -> {
                    mutableConfirmation.value = null
                    store.sessionId = -1
                    val error = when (status) {
                        PackageInstaller.STATUS_FAILURE_ABORTED -> UpdateFailure.INSTALL_CANCELLED
                        PackageInstaller.STATUS_FAILURE_STORAGE -> UpdateFailure.STORAGE
                        PackageInstaller.STATUS_FAILURE_BLOCKED -> UpdateFailure.INSTALL_BLOCKED
                        PackageInstaller.STATUS_FAILURE_CONFLICT, PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> UpdateFailure.INCOMPATIBLE
                        PackageInstaller.STATUS_FAILURE_INVALID -> UpdateFailure.VERIFICATION
                        else -> UpdateFailure.INSTALL_FAILED
                    }
                    mutableOutcome.value = InstallOutcome(UpdatePhase.READY, error)
                }
            }
        } finally { finished() }
    }

    fun confirmationLaunched() {
        mutableConfirmation.value = null
        context.getSystemService(NotificationManager::class.java).cancel(5680)
    }

    private fun notifyApproval(intent: Intent) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel("update_install", context.getString(R.string.app_updates_title), NotificationManager.IMPORTANCE_DEFAULT))
        val pending = PendingIntent.getActivity(context, 5680, intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, "update_install").setSmallIcon(R.drawable.convx_notification)
            .setContentTitle(context.getString(R.string.update_ready)).setContentText(context.getString(R.string.updater_approval))
            .setContentIntent(pending).setAutoCancel(true).build()
        runCatching { manager.notify(5680, notification) }
    }
}

@AndroidEntryPoint
class UpdateInstallReceiver : BroadcastReceiver() {
    @Inject lateinit var installer: UpdateInstaller
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        installer.onResult(intent, pending::finish)
    }
}
