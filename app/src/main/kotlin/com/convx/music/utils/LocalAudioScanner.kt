/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.convx.music.constants.LocalExcludedFoldersKey
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.SongAlbumMap
import com.convx.music.db.entities.SongArtistMap
import com.convx.music.db.entities.SongEntity
import com.convx.music.playback.DownloadExporter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

private fun localDateTimeFromEpochSeconds(seconds: Long): LocalDateTime =
    Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).toLocalDateTime()

object LocalAudioScanner {

    private const val MIN_DURATION_MS = 15_000L // 15 seconds - skip ringtones, notifications
    private val ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")

    data class ScanResult(
        val totalFound: Int,
        val newSongs: Int,
        val skippedExisting: Int,
    )

    // Read exclusions here so every caller applies the same folder settings.
    suspend fun scanAndInsert(
        context: Context,
        database: MusicDatabase,
    ): ScanResult =
        withContext(Dispatchers.IO) {
            var totalFound = 0
            var newSongs = 0
            var skippedExisting = 0

            val excludedFolders = decodeExcludedFolders(
                context.dataStore.data.first()[LocalExcludedFoldersKey] ?: "",
            )

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.YEAR,
                LocalFolderIndex.pathColumn,
            )

            // IS_MUSIC alone misses files whose scanner flag isn't set (common
            // for FLAC/OGG/OPUS from downloads) — accept anything with an
            // audio/* mime type too. Duration floor still filters ringtones.
            // Exported downloads are already stored as online songs in the library.
            val downloadExclusion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                " AND (${MediaStore.MediaColumns.RELATIVE_PATH} IS NULL OR (" +
                    "${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE '${DownloadExporter.FOLDER}/%'" +
                    " AND ${MediaStore.MediaColumns.RELATIVE_PATH} NOT LIKE 'Music/Convx/%'))"
            } else {
                ""
            }
            val selection =
                "(${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.MIME_TYPE} LIKE 'audio/%')" +
                    " AND ${MediaStore.Audio.Media.DURATION} >= ?" + downloadExclusion
            val selectionArgs = arrayOf(MIN_DURATION_MS.toString())
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            // EXTERNAL_CONTENT_URI only covers the primary shared volume, so
            // songs on an SD card / secondary volume were never found. Query
            // every mounted external volume (API 29+); pre-29 has just the one.
            val collections = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.getExternalVolumeNames(context).map { volume ->
                    MediaStore.Audio.Media.getContentUri(volume)
                }
            } else {
                listOf(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            }

            // Load IDs once to avoid a Room query for every MediaStore row.
            // Fetch full entities only when removing or backfilling a song.
            val existingIds = database.localSongIds().toHashSet()

            collections.forEach { collection ->
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder,
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                    val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                    val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                    val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                    val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                    val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
                    val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                    val pathColumnIndex = cursor.getColumnIndexOrThrow(LocalFolderIndex.pathColumn)

                    while (cursor.moveToNext()) {
                        totalFound++
                        val id = cursor.getLong(idColumn)
                        val title = cursor.getString(titleColumn) ?: "Unknown"
                        val artistName = cursor.getString(artistColumn) ?: "Unknown Artist"
                        val albumName = cursor.getString(albumColumn)
                        val albumId = cursor.getLong(albumIdColumn)
                        val durationMs = cursor.getLong(durationColumn)
                        val dateAdded = cursor.getLong(dateAddedColumn)
                        val dateModified = cursor.getLong(dateModifiedColumn)
                        val year = cursor.getInt(yearColumn)
                        val folderPath = cursor.getString(pathColumnIndex)
                            ?.let { LocalFolderIndex.folderPathOf(it) }
                            ?: ""

                        val contentUri = ContentUris.withAppendedId(
                            collection,
                            id,
                        ).toString()

                        val albumArtUri = if (albumId > 0) {
                            ContentUris.withAppendedId(ALBUM_ART_URI, albumId).toString()
                        } else {
                            null
                        }

                        val songId = contentUri

                        // Prefix matching excludes nested folders too.
                        if (isExcludedFolder(folderPath, excludedFolders)) {
                            removeExcludedSong(database, songId, existingIds)
                            continue
                        }

                        if (songId in existingIds) {
                            backfillExistingSongDates(database, songId, dateAdded, dateModified)
                            skippedExisting++
                            continue
                        }
                        existingIds.add(songId)

                        val inserted = insertNewSong(
                            database = database,
                            songId = songId,
                            title = title,
                            artistName = artistName,
                            albumName = albumName,
                            albumArtUri = albumArtUri,
                            durationMs = durationMs,
                            year = year,
                            dateAdded = dateAdded,
                            dateModified = dateModified,
                        )
                        if (inserted) {
                            newSongs++
                        }
                    }
                }
            }

            Timber.tag("LocalAudioScanner")
                .i("Scan complete: totalFound=$totalFound, newSongs=$newSongs, skippedExisting=$skippedExisting")

            ScanResult(
                totalFound = totalFound,
                newSongs = newSongs,
                skippedExisting = skippedExisting,
            )
        }

    private suspend fun removeExcludedSong(
        database: MusicDatabase,
        songId: String,
        existingIds: MutableSet<String>,
    ) {
        if (songId !in existingIds) return

        database.getSongById(songId)?.let { database.delete(it.song) }
        existingIds.remove(songId)
    }

    private suspend fun backfillExistingSongDates(
        database: MusicDatabase,
        songId: String,
        dateAdded: Long,
        dateModified: Long,
    ) {
        runCatching {
            database.backfillLocalSongDates(
                songId = songId,
                dateAdded = localDateTimeFromEpochSeconds(dateAdded),
                dateModified = localDateTimeFromEpochSeconds(dateModified),
            )
        }
    }

    private suspend fun insertNewSong(
        database: MusicDatabase,
        songId: String,
        title: String,
        artistName: String,
        albumName: String,
        albumArtUri: String?,
        durationMs: Long,
        year: Int,
        dateAdded: Long,
        dateModified: Long,
    ): Boolean {
        val artistNames = ArtistSplitter.split(artistName).ifEmpty { listOf(artistName) }
        val albumId = "local_album_${albumName.hashCode()}"
        val durationSeconds = (durationMs / 1000).toInt()
        val song = SongEntity(
            id = songId,
            title = title,
            duration = durationSeconds,
            thumbnailUrl = albumArtUri,
            albumId = albumId.takeIf { albumName.isNotBlank() },
            albumName = albumName,
            year = year.takeIf { it > 0 },
            dateModified = localDateTimeFromEpochSeconds(dateModified),
            isLocal = true,
            // Use MediaStore's original date instead of the scan time.
            inLibrary = localDateTimeFromEpochSeconds(dateAdded),
        )

        return try {
            // Song, artist, and album rows reference each other, so save them atomically.
            database.withTransaction {
                upsert(song)
                artistNames.forEachIndexed { index, name ->
                    val artistId = "local_artist_${name.hashCode()}"
                    insert(ArtistEntity(id = artistId, name = name, isLocal = true))
                    insert(SongArtistMap(songId = songId, artistId = artistId, position = index))
                }
                if (albumName.isNotBlank()) {
                    insert(
                        AlbumEntity(
                            id = albumId,
                            title = albumName,
                            thumbnailUrl = albumArtUri,
                            songCount = 1,
                            duration = durationSeconds,
                            year = year.takeIf { it > 0 },
                            isLocal = true,
                            inLibrary = LocalDateTime.now(),
                        ),
                    )
                    insert(SongAlbumMap(songId = songId, albumId = albumId, index = 0))
                }
            }
            true
        } catch (e: Exception) {
            Timber.tag("LocalAudioScanner").w(e, "Failed to insert $title")
            false
        }
    }

    private fun isExcludedFolder(folderPath: String, excludedFolders: Set<String>): Boolean =
        excludedFolders.any { excluded ->
            folderPath == excluded || (
                folderPath.startsWith(excluded) && folderPath.getOrNull(excluded.length) == '/'
            )
        }
}
