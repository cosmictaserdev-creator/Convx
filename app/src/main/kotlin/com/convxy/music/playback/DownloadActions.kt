/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService

/*
 * One place that decides which songs a bulk download action actually touches.
 *
 * Every screen used to inline `songs.forEach { sendAddDownload(...) }` and
 * `songs.forEach { sendRemoveDownload(...) }` over the whole list, with no regard for what
 * state each download was already in. Two user-visible bugs fell out of that, and because
 * the loop was copy-pasted into sixteen files both bugs existed sixteen times:
 *
 * - Tapping the "Downloading" row to cancel removed *every* download in the list,
 *   including the ones that had already finished, deleting files the user had waited for.
 * - Tapping download again after a network drop re-queued the whole list, because
 *   `DownloadManager.addDownload` moves even a COMPLETED download back to QUEUED, so the
 *   already-downloaded songs were fetched a second time.
 *
 * The filtering predicates below are pure and unit-tested; the send* wrappers are the thin
 * Android-facing shell around them.
 */

/** What a bulk download action needs to know about one song. */
data class DownloadTarget(
    val id: String,
    val title: String,
)

/**
 * Whether [state] means a download should be (re)queued.
 *
 * `null` is a song we have never downloaded. FAILED and STOPPED are the retry cases — a
 * network drop leaves downloads there, and re-queuing them is the entire point of tapping
 * download again. COMPLETED is skipped so a retry does not refetch finished songs;
 * QUEUED/DOWNLOADING/RESTARTING are skipped because the work is already scheduled.
 */
fun shouldQueueDownload(state: Int?): Boolean = when (state) {
    null, Download.STATE_FAILED, Download.STATE_STOPPED -> true
    else -> false
}

/**
 * Whether [state] means a *cancel* should remove this download.
 *
 * COMPLETED is deliberately excluded: cancelling in-flight work must never delete files
 * that already finished. Deleting a finished download is a separate, explicit action —
 * see [removeDownloads].
 */
fun shouldCancelDownload(state: Int?): Boolean = when (state) {
    Download.STATE_QUEUED,
    Download.STATE_DOWNLOADING,
    Download.STATE_FAILED,
    Download.STATE_STOPPED,
    Download.STATE_RESTARTING,
    -> true

    else -> false
}

/** Queue [songs] for download, skipping any that are already downloaded or in flight. */
@UnstableApi
fun downloadSongs(
    context: Context,
    songs: List<DownloadTarget>,
    downloads: Map<String, Download>,
) {
    songs.forEach { song ->
        if (!shouldQueueDownload(downloads[song.id]?.state)) return@forEach
        DownloadService.sendAddDownload(
            context,
            ExoDownloadService::class.java,
            DownloadRequest
                .Builder(song.id, song.id.toUri())
                .setCustomCacheKey(song.id)
                .setData(song.title.toByteArray())
                .build(),
            false,
        )
    }
}

/** Queue a single song, skipping it if it is already downloaded or in flight. */
@UnstableApi
fun downloadSong(
    context: Context,
    id: String,
    title: String,
    downloads: Map<String, Download>,
) = downloadSongs(context, listOf(DownloadTarget(id, title)), downloads)

/**
 * Stop pending and in-flight downloads for [ids]. Songs that already finished downloading
 * are left on disk.
 */
@UnstableApi
fun cancelDownloads(
    context: Context,
    ids: List<String>,
    downloads: Map<String, Download>,
) {
    ids.forEach { id ->
        if (!shouldCancelDownload(downloads[id]?.state)) return@forEach
        DownloadService.sendRemoveDownload(context, ExoDownloadService::class.java, id, false)
    }
}

/**
 * Delete downloads for [ids] whatever state they are in.
 *
 * This is the explicit "remove download" action, reached from a confirmation dialog. Do
 * not call it to cancel an in-progress download — use [cancelDownloads].
 */
@UnstableApi
fun removeDownloads(
    context: Context,
    ids: List<String>,
) {
    ids.forEach { id ->
        DownloadService.sendRemoveDownload(context, ExoDownloadService::class.java, id, false)
    }
}
