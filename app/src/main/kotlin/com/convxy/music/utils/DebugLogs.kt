/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import com.convxy.music.BuildConfig
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Timber tree that mirrors every log line into a file under cacheDir, so a
 * debug build's full history (including onPlayerError detail lines) is
 * shareable from the app without adb. File rotates once it exceeds [MAX_FILE_BYTES].
 */
class FileLoggingTree(private val logFile: File) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            synchronized(this) {
                val line = buildString {
                    append(timestampFormatter.format(Date()))
                    append(' ')
                    append(levelName(priority))
                    append('/')
                    append(tag ?: "Timber")
                    append(": ")
                    append(message)
                    if (t != null) {
                        append('\n')
                        append(Log.getStackTraceString(t))
                    }
                    append('\n')
                }
                val old = File(logFile.parentFile, "${logFile.name}.old")
                if (logFile.exists() && logFile.length() + line.length > MAX_FILE_BYTES) {
                    old.delete()
                    logFile.renameTo(old)
                }
                logFile.appendText(line)
            }
        } catch (e: IOException) {
            // Logging must never break the app
        }
    }

    private fun levelName(priority: Int): String = when (priority) {
        Log.VERBOSE -> "V"
        Log.DEBUG -> "D"
        Log.INFO -> "I"
        Log.WARN -> "W"
        Log.ERROR -> "E"
        Log.ASSERT -> "A"
        else -> "?"
    }

    companion object {
        private const val MAX_FILE_BYTES = 5 * 1024 * 1024

        // Only touched inside the synchronized(log) block in log()
        private val timestampFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    }
}

/**
 * Helpers for building a shareable log blob from the app itself: device header
 * + full Timber history + in-app playback event log, shipped via FileProvider.
 */
object DebugLogs {
    const val FILE_NAME = "vivi_log.txt"
    private const val MAX_SHARE_CHARS = 400_000

    fun file(context: Context): File {
        val dir = File(context.cacheDir, "logs").apply { mkdirs() }
        return File(dir, FILE_NAME)
    }

    fun tree(context: Context): FileLoggingTree = FileLoggingTree(file(context))

    private fun deviceHeader(context: Context): String = buildString {
        appendLine("Convxy Debug Log")
        appendLine("=".repeat(50))
        appendLine("Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
        appendLine("Manufacturer: ${Build.MANUFACTURER}")
        appendLine("Device: ${Build.MODEL}")
        appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        appendLine("App: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        appendLine("Build type: ${BuildConfig.BUILD_TYPE} (debug=${BuildConfig.DEBUG})")
        appendLine("Flavor: ${BuildConfig.FLAVOR}")
        appendLine("Arch: ${Build.SUPPORTED_ABIS.joinToString()}")
    }

    fun buildBody(context: Context, playbackLogs: List<PlaybackLogEntry> = emptyList()): String {
        val file = file(context)
        return buildString {
            append(deviceHeader(context))
            appendLine()
            if (file.exists()) {
                appendLine("=== APP LOG (Timber) ===")
                append(file.readText().takeLast(MAX_SHARE_CHARS))
            } else {
                appendLine("(no Timber file captured yet)")
            }
            if (playbackLogs.isNotEmpty()) {
                appendLine()
                appendLine("=== PLAYBACK EVENTS ===")
                playbackLogs.forEach {
                    appendLine("${it.timestamp} [${it.level.name}] ${it.message}${it.details?.let { d -> " -- $d" } ?: ""}")
                }
            }
        }
    }

    fun share(context: Context, playbackLogs: List<PlaybackLogEntry> = emptyList()) {
        val body = buildBody(context, playbackLogs)
        try {
            val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val shareFile = File(context.cacheDir, "vivi_log_$ts.txt")
            shareFile.writeText(body)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.FileProvider", shareFile)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "vivi-music debug log")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share debug log"))
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, body)
                putExtra(Intent.EXTRA_SUBJECT, "vivi-music debug log")
            }
            context.startActivity(Intent.createChooser(intent, "Share debug log"))
        }
    }
}
