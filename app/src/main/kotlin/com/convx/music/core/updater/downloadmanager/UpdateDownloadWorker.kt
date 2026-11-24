package com.convx.music.core.updater.downloadmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.convx.music.MainActivity
import com.convx.music.R
import com.convx.music.core.updater.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class UpdateDownloadWorker(private val context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val asset = runCatching { Json.decodeFromString<UpdateAsset>(inputData.getString("asset") ?: "") }.getOrNull()
            ?: return@withContext failure(UpdateFailure.METADATA)
        if (!GitHubReleaseSource.isTrustedUrl(asset.url) || asset.size <= 0 || !UpdatePolicy.validDigest(asset.sha256))
            return@withContext failure(UpdateFailure.METADATA)
        val directory = File(context.noBackupFilesDir, "updates").apply { mkdirs() }
        val partial = File(directory, "${asset.sha256}.part")
        val validator = File(directory, "${asset.sha256}.etag")
        val complete = File(directory, "${asset.sha256}.apk")
        try {
            if (directory.usableSpace < (asset.size - partial.length()).coerceAtLeast(0) + 32L * 1024 * 1024)
                return@withContext failure(UpdateFailure.STORAGE)
            setForeground(notification(0))
            if (!complete.exists()) {
                val savedValidator = validator.takeIf { it.isFile }?.readText()?.takeIf { it.startsWith('"') && !it.startsWith("W/") }
                var offset = if (savedValidator != null && partial.length() in 1 until asset.size) partial.length() else 0L
                if (offset == 0L) partial.delete()
                val connection = open(asset.url, offset, savedValidator)
                try {
                    val code = connection.responseCode
                    if (code in setOf(408, 429, 500, 502, 503, 504)) return@withContext if (runAttemptCount < 4) Result.retry() else failure(UpdateFailure.NETWORK)
                    if (code == 416 && offset > 0) {
                        partial.delete(); validator.delete()
                        return@withContext if (runAttemptCount < 4) Result.retry() else failure(UpdateFailure.NETWORK)
                    }
                    if (code != 200 && code != 206) return@withContext failure(UpdateFailure.NETWORK)
                    if (code == 200) offset = 0
                    if (code == 206) {
                        val range = connection.getHeaderField("Content-Range") ?: ""
                        if (!range.startsWith("bytes $offset-") || !range.endsWith("/${asset.size}")) throw UpdateException(UpdateFailure.VERIFICATION)
                        if (offset > 0 && connection.getHeaderField("ETag") != savedValidator) throw UpdateException(UpdateFailure.VERIFICATION)
                    }
                    connection.getHeaderField("ETag")?.takeIf { !it.startsWith("W/") }?.let { validator.writeText(it) }
                    connection.inputStream.use { input ->
                        FileOutputStream(partial, offset > 0).use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var downloaded = offset
                            var lastProgress = 0L
                            while (true) {
                                currentCoroutineContext().ensureActive()
                                val count = input.read(buffer)
                                if (count < 0) break
                                downloaded += count
                                if (downloaded > asset.size) throw UpdateException(UpdateFailure.VERIFICATION)
                                output.write(buffer, 0, count)
                                val now = android.os.SystemClock.elapsedRealtime()
                                if (now - lastProgress >= 500) {
                                    lastProgress = now
                                    val progress = downloaded.toFloat() / asset.size
                                    setProgress(workDataOf("progress" to progress, "verifying" to false))
                                    setForeground(notification((progress * 100).toInt()))
                                }
                            }
                            output.fd.sync()
                        }
                    }
                } finally { connection.disconnect() }
                if (partial.length() != asset.size) throw IOException("Incomplete transfer")
                setProgress(workDataOf("verifying" to true))
                ApkVerifier(context).verify(partial, asset)
                Files.move(partial.toPath(), complete.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                validator.delete()
            } else ApkVerifier(context).verify(complete, asset)
            Result.success(workDataOf("file_path" to complete.absolutePath))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: UpdateException) {
            partial.delete(); validator.delete(); complete.delete()
            failure(error.failure)
        } catch (_: IOException) {
            if (runAttemptCount < 4) Result.retry() else failure(UpdateFailure.NETWORK)
        } catch (_: SecurityException) {
            failure(UpdateFailure.STORAGE)
        }
    }

    private fun open(url: String, offset: Long, etag: String?): HttpURLConnection {
        var target = url
        repeat(6) {
            if (!GitHubReleaseSource.isTrustedUrl(target)) throw UpdateException(UpdateFailure.METADATA)
            val connection = URI(target).toURL().openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.setRequestProperty("Accept-Encoding", "identity")
            if (offset > 0 && etag != null) {
                connection.setRequestProperty("Range", "bytes=$offset-")
                connection.setRequestProperty("If-Range", etag)
            }
            try {
                if (connection.responseCode !in setOf(301, 302, 303, 307, 308)) return connection
                target = URI(target).resolve(connection.getHeaderField("Location")).toString()
            } catch (error: Exception) {
                connection.disconnect()
                throw error
            }
            connection.disconnect()
        }
        throw IOException("Too many redirects")
    }

    private fun notification(progress: Int): ForegroundInfo {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, context.getString(R.string.download_progress_channel), NotificationManager.IMPORTANCE_LOW))
        val open = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java).putExtra("open_update", true), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.convx_notification).setContentTitle(context.getString(R.string.downloading_update))
            .setProgress(100, progress, false).setOngoing(true).setOnlyAlertOnce(true).setContentIntent(open)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.cancel), WorkManager.getInstance(context).createCancelPendingIntent(id)).build()
        return if (Build.VERSION.SDK_INT >= 29) ForegroundInfo(5679, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC) else ForegroundInfo(5679, notification)
    }

    private fun failure(error: UpdateFailure) = Result.failure(workDataOf("error" to error.name))
    companion object { private const val CHANNEL = "update_transfer" }
}
