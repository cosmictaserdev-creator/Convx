/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.room.withTransaction
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.SongAlbumMap
import com.convx.music.db.entities.SongArtistMap
import java.io.File
import java.time.LocalDateTime
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag

/**
 * Reads and writes the tags inside a local audio file (ID3, Vorbis/FLAC, MP4, Ogg) and keeps the library in step.
 *
 * Android hands out content:// URIs, not paths, and the tag library wants a file. So the song is copied to cache,
 * edited there, and written back through the same URI. The original is kept until the write-back succeeds, so a
 * write that dies half way can be undone instead of leaving a truncated song.
 */
object LocalTagEditor {

    data class Tags(
        val title: String = "",
        val artist: String = "",
        val album: String = "",
        val year: String = "",
        val track: String = "",
        val genre: String = "",
    )

    init {
        // The tag library logs every field it skips; on a phone that is only noise.
        runCatching { Logger.getLogger("org.jaudiotagger").level = Level.OFF }
    }

    /** The tags currently in the file, or null if it has none the library can read. */
    suspend fun read(context: Context, uri: Uri): Tags? = withContext(Dispatchers.IO) {
        runCatching {
            withCopy(context, uri) { file ->
                val tag = AudioFileIO.read(file).tag ?: return@withCopy null
                Tags(
                    title = tag.first(FieldKey.TITLE),
                    artist = tag.first(FieldKey.ARTIST),
                    album = tag.first(FieldKey.ALBUM),
                    year = tag.first(FieldKey.YEAR),
                    track = tag.first(FieldKey.TRACK),
                    genre = tag.first(FieldKey.GENRE),
                )
            }
        }.getOrNull()
    }

    /** Writes [tags] into the file. The caller must already hold write access to [uri]. */
    suspend fun write(context: Context, uri: Uri, tags: Tags): Boolean = withContext(Dispatchers.IO) {
        val original = File(context.cacheDir, "tagedit_backup_${System.nanoTime()}")
        val working = File(context.cacheDir, "tagedit_${System.nanoTime()}.${extensionFor(context, uri)}")
        try {
            val resolver = context.contentResolver
            resolver.openInputStream(uri)?.use { input ->
                original.outputStream().use { input.copyTo(it) }
            } ?: return@withContext false
            original.copyTo(working, overwrite = true)

            val audio = AudioFileIO.read(working)
            val tag = audio.tagOrCreateAndSetDefault
            tag.put(FieldKey.TITLE, tags.title)
            tag.put(FieldKey.ARTIST, tags.artist)
            tag.put(FieldKey.ALBUM, tags.album)
            tag.put(FieldKey.YEAR, tags.year)
            tag.put(FieldKey.TRACK, tags.track)
            tag.put(FieldKey.GENRE, tags.genre)
            audio.commit()

            try {
                resolver.openOutputStream(uri, "wt")?.use { out -> working.inputStream().use { it.copyTo(out) } }
                    ?: return@withContext false
            } catch (failure: Exception) {
                // The truncating open already happened; put the original bytes back.
                runCatching { resolver.openOutputStream(uri, "wt")?.use { out -> original.inputStream().use { it.copyTo(out) } } }
                throw failure
            }
            // Keep the system's own index in step so other apps see the new tags without a rescan.
            runCatching {
                resolver.update(
                    uri,
                    ContentValues().apply {
                        put(MediaStore.Audio.Media.TITLE, tags.title)
                        put(MediaStore.Audio.Media.ARTIST, tags.artist)
                        put(MediaStore.Audio.Media.ALBUM, tags.album)
                    },
                    null,
                    null,
                )
            }
            true
        } catch (_: Exception) {
            false
        } finally {
            original.delete()
            working.delete()
        }
    }

    /**
     * Re-files a local song under the artist and album its new tags name, using the same ids the scanner gives
     * them so it joins any other songs already there.
     */
    suspend fun applyToLibrary(database: MusicDatabase, songId: String, tags: Tags) {
        val artistName = tags.artist.trim().ifEmpty { "Unknown Artist" }
        val albumName = tags.album.trim()
        val artistId = "local_artist_${artistName.hashCode()}"
        val albumId = "local_album_${albumName.hashCode()}"
        val year = tags.year.trim().take(4).toIntOrNull()
        val trackNumber = tags.track.substringBefore('/').trim().toIntOrNull() ?: 0
        database.withTransaction {
            val existing = getSongById(songId)?.song ?: return@withTransaction
            upsert(
                existing.copy(
                    title = tags.title.trim().ifEmpty { existing.title },
                    albumId = albumId.takeIf { albumName.isNotEmpty() },
                    albumName = albumName.takeIf { it.isNotEmpty() },
                    year = year ?: existing.year,
                ),
            )
            insert(ArtistEntity(id = artistId, name = artistName, isLocal = true))
            clearSongArtists(songId)
            insert(SongArtistMap(songId = songId, artistId = artistId, position = 0))
            clearSongAlbums(songId)
            if (albumName.isNotEmpty()) {
                insert(
                    AlbumEntity(
                        id = albumId,
                        title = albumName,
                        thumbnailUrl = existing.thumbnailUrl,
                        songCount = 1,
                        duration = existing.duration.coerceAtLeast(0),
                        year = year,
                        isLocal = true,
                        inLibrary = LocalDateTime.now(),
                    ),
                )
                insert(SongAlbumMap(songId = songId, albumId = albumId, index = trackNumber))
            }
            pruneEmptyLocalAlbums()
            pruneEmptyLocalArtists()
        }
    }

    private fun Tag.first(key: FieldKey): String = runCatching { getFirst(key) }.getOrDefault("").orEmpty()

    /** An empty value removes the field instead of writing a blank one. */
    private fun Tag.put(key: FieldKey, value: String) {
        val text = value.trim()
        if (text.isEmpty()) runCatching { deleteField(key) } else setField(key, text)
    }

    private fun <T> withCopy(context: Context, uri: Uri, block: (File) -> T): T {
        val copy = File(context.cacheDir, "tagread_${System.nanoTime()}.${extensionFor(context, uri)}")
        try {
            context.contentResolver.openInputStream(uri)?.use { input -> copy.outputStream().use { input.copyTo(it) } }
                ?: error("Cannot open $uri")
            return block(copy)
        } finally {
            copy.delete()
        }
    }

    /** The tag library picks its parser from the extension, so it has to be right. */
    private fun extensionFor(context: Context, uri: Uri): String {
        val name = runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) it.getString(0) else null
            }
        }.getOrNull().orEmpty()
        val fromName = name.substringAfterLast('.', "").lowercase()
        if (fromName in KNOWN_EXTENSIONS) return fromName
        return when (context.contentResolver.getType(uri)?.lowercase()) {
            "audio/mpeg", "audio/mp3" -> "mp3"
            "audio/flac", "audio/x-flac" -> "flac"
            "audio/mp4", "audio/x-m4a", "audio/aac" -> "m4a"
            "audio/ogg", "application/ogg" -> "ogg"
            "audio/opus" -> "opus"
            "audio/wav", "audio/x-wav" -> "wav"
            else -> "mp3"
        }
    }

    private val KNOWN_EXTENSIONS = setOf("mp3", "flac", "m4a", "mp4", "ogg", "oga", "opus", "wav", "aif", "aiff", "wma")
}
