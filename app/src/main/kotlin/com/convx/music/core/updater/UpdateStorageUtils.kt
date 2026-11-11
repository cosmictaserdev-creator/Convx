package com.convx.music.core.updater

import android.content.Context
import java.io.File

fun getDownloadedApksDir(context: Context): File {
    return File(context.noBackupFilesDir, "updates")
}

fun getDownloadedApkCount(context: Context): Int {
    val dir = getDownloadedApksDir(context)
    if (!dir.exists() || !dir.isDirectory) return 0
    return dir.listFiles { file ->
        file.isFile && file.name.endsWith(".apk", ignoreCase = true)
    }?.size ?: 0
}

fun clearDownloadedApks(context: Context): Boolean {
    val dir = getDownloadedApksDir(context)
    if (!dir.exists() || !dir.isDirectory) return true
    var allDeleted = true
    dir.listFiles { file ->
        file.isFile && file.name.endsWith(".apk", ignoreCase = true)
    }?.forEach { file ->
        if (!file.delete()) {
            allDeleted = false
        }
    }
    return allDeleted
}

fun autoClearOldApks(context: Context) {
    val dir = getDownloadedApksDir(context)
    if (!dir.exists() || !dir.isDirectory) return
    val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
    dir.listFiles { file ->
        file.isFile && (file.name.endsWith(".part", ignoreCase = true) || file.name.endsWith(".etag", ignoreCase = true)) && file.lastModified() < oneDayAgo
    }?.forEach { it.delete() }
}
