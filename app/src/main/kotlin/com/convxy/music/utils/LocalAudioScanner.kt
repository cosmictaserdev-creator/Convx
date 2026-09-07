/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.convxy.music.constants.LocalExcludedFoldersKey
import com.convxy.music.db.MusicDatabase
import com.convxy.music.db.entities.AlbumEntity
import com.convxy.music.db.entities.ArtistEntity
import com.convxy.music.db.entities.SongAlbumMap
import com.convxy.music.db.entities.SongArtistMap
import com.convxy.music.db.entities.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

object LocalAudioScanner {

    private const val MIN_DURATION_MS = 15_000L // 15 seconds - skip ringtones, notifications

    data class ScanResult(
        val totalFound: Int,
        val newSongs: Int,
        val skippedExisting: Int,
    )

    /**
     * Excluded folders are read here rather than passed in. They used to be a parameter
     * defaulting to an empty set, and two of the four callers (Home's pull-to-refresh and
     * the auto-playlist scan) never passed it — so excluding a folder in settings dropped
     * its songs, and then the very next refresh imported every one of them straight back.
     * Reading the preference at the one place that acts on it makes every caller correct.
     */
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
            val selection =
                "(${MediaStore.Audio.Media.IS_MUSIC} != 0 OR ${MediaStore.Audio.Media.MIME_TYPE} LIKE 'audio/%')" +
                    " AND ${MediaStore.Audio.Media.DURATION} >= ?"
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
                        ContentUris.withAppendedId(
                            Uri.parse("content://media/external/audio/albumart"),
                            albumId,
                        ).toString()
                    } else {
                        null
                    }

                    val songId = contentUri

                    // Check if song already exists
                    val existingSong = database.getSongById(songId)

                    // Prefix match, not equality: excluding "Music/Podcasts" has to take
                    // its subfolders with it, and a folder picked by hand in settings is
                    // usually a parent of the ones MediaStore actually reports.
                    if (folderPath != null && excludedFolders.any {
                            folderPath == it || folderPath.startsWith("$it/")
                        }
                    ) {
                        // Already imported from a folder that's since been excluded —
                        // remove it so toggling the folder off actually takes effect,
                        // not just "no new songs from here" going forward.
                        if (existingSong != null) database.delete(existingSong.song)
                        continue
                    }

                    if (existingSong != null) {
                        // Rows written before the scanner recorded DATE_ADDED have a null
                        // inLibrary, and this branch used to skip them forever — so
                        // "Recently added" stayed a no-op for anyone whose library was
                        // scanned by an older build, however many times they rescanned.
                        // Backfill the dates only; everything else about the row is left
                        // alone so a rescan never clobbers user edits.
                        if (existingSong.song.inLibrary == null) {
                            runCatching {
                                database.withTransaction {
                                    upsert(
                                        existingSong.song.copy(
                                            inLibrary = java.time.Instant.ofEpochSecond(dateAdded)
                                                .atZone(java.time.ZoneId.systemDefault())
                                                .toLocalDateTime(),
                                            dateModified = existingSong.song.dateModified
                                                ?: java.time.Instant.ofEpochSecond(dateModified)
                                                    .atZone(java.time.ZoneId.systemDefault())
                                                    .toLocalDateTime(),
                                        ),
                                    )
                                }
                            }
                        }
                        skippedExisting++
                        continue
                    }

                    val artistId = "local_artist_${artistName.hashCode()}"
                    val albumIdStr = "local_album_${albumName.hashCode()}"

                    // Insert song entity
                    val songEntity = SongEntity(
                        id = songId,
                        title = title,
                        duration = (durationMs / 1000).toInt(),
                        thumbnailUrl = albumArtUri,
                        albumId = if (albumName.isNotBlank()) albumIdStr else null,
                        albumName = albumName,
                        year = if (year > 0) year else null,
                        dateModified = java.time.Instant.ofEpochSecond(dateModified)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime(),
                        isLocal = true,
                        // The file's real MediaStore DATE_ADDED, not the scan time —
                        // scanning stamps every song with the same instant, which makes
                        // "Date added" sorting meaningless. dateAdded was already being
                        // read here and discarded.
                        inLibrary = java.time.Instant.ofEpochSecond(dateAdded)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime(),
                    )

                    // Song + artist + mapping in ONE transaction: the previous
                    // separate database.query {} blocks were posted to Room's
                    // multi-threaded query executor with no ordering guarantee,
                    // so the SongArtistMap insert could run before the song
                    // upsert committed and die on the foreign-key constraint —
                    // uncaught on the executor thread, crashing the whole app.
                    // withTransaction is serial, atomic, and throws HERE where
                    // we can catch it, so one bad row skips instead of killing
                    // the scan.
                    try {
                        database.withTransaction {
                            upsert(songEntity)
                            insert(
                                ArtistEntity(
                                    id = artistId,
                                    name = artistName,
                                    isLocal = true,
                                )
                            )
                            insert(
                                SongArtistMap(
                                    songId = songId,
                                    artistId = artistId,
                                    position = 0,
                                )
                            )
                            // Create album entity + mapping so local albums appear in library
                            if (albumName.isNotBlank()) {
                                insert(
                                    AlbumEntity(
                                        id = albumIdStr,
                                        title = albumName,
                                        thumbnailUrl = albumArtUri,
                                        songCount = 1,
                                        duration = (durationMs / 1000).toInt(),
                                        year = if (year > 0) year else null,
                                        isLocal = true,
                                        inLibrary = java.time.LocalDateTime.now(),
                                    )
                                )
                                insert(
                                    SongAlbumMap(
                                        songId = songId,
                                        albumId = albumIdStr,
                                        index = 0,
                                    )
                                )
                            }
                        }
                        newSongs++
                    } catch (e: Exception) {
                        Timber.tag("LocalAudioScanner").w(e, "Failed to insert $title")
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
}
