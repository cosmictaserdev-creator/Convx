/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Groups on-device audio by the folder it lives in.
 *
 * The song table has no path column (an old migration dropped `localPath`), so
 * folders are read straight from MediaStore instead of the database. Song ids
 * are built exactly the way [LocalAudioScanner] builds them — the content URI —
 * so a folder's ids can be handed to `songsByIds` to get the scanned rows back.
 */
object LocalFolderIndex {

    private const val MIN_DURATION_MS = 15_000L

    data class Folder(
        val path: String,
        val name: String,
        val songIds: List<String>,
    )

    /**
     * The column to query for a song's folder, and how to turn its raw value into
     * a bare directory path — shared with [LocalAudioScanner] so both derive the
     * exact same folder for the exact same song.
     */
    val pathColumn: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.RELATIVE_PATH
        } else {
            MediaStore.Audio.Media.DATA
        }

    /** RELATIVE_PATH is already a directory ("Music/Rock/"); DATA is the file
     *  itself, so drop the file name. */
    fun folderPathOf(raw: String): String {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) raw else raw.substringBeforeLast('/', "")
        return dir.trim('/')
    }

    // ponytail: process-lifetime cache, reloaded on demand. Folders only change
    // when files do, and a rescan/app restart already refreshes it.
    @Volatile
    private var cache: List<Folder> = emptyList()

    suspend fun load(context: Context): List<Folder> = withContext(Dispatchers.IO) {
        val projection = arrayOf(MediaStore.Audio.Media._ID, pathColumn)

        // Same predicate as the scanner, so folder counts match what was imported.
        val selection =
            "(${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.MIME_TYPE} LIKE 'audio/%')" +
                " AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(MIN_DURATION_MS.toString())

        val collections = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.getExternalVolumeNames(context).map { MediaStore.Audio.Media.getContentUri(it) }
        } else {
            listOf(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        }

        val byPath = linkedMapOf<String, MutableList<String>>()

        collections.forEach { collection ->
            runCatching {
                context.contentResolver.query(collection, projection, selection, selectionArgs, null)
            }.getOrNull()?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val pathIndex = cursor.getColumnIndexOrThrow(pathColumn)
                while (cursor.moveToNext()) {
                    val raw = cursor.getString(pathIndex) ?: continue
                    val path = folderPathOf(raw)
                    if (path.isEmpty()) continue
                    val id = ContentUris.withAppendedId(collection, cursor.getLong(idColumn)).toString()
                    byPath.getOrPut(path) { mutableListOf() }.add(id)
                }
            }
        }

        byPath
            .map { (path, ids) -> Folder(path = path, name = path.substringAfterLast('/'), songIds = ids) }
            .sortedBy { it.name.lowercase() }
            .also { cache = it }
    }

    /** Ids for one folder, loading the index first if this process hasn't yet. */
    suspend fun songIdsFor(context: Context, path: String): List<String> =
        (cache.ifEmpty { load(context) }).firstOrNull { it.path == path }?.songIds.orEmpty()
}
