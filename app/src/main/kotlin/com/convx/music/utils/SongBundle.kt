/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.content.Context
import android.net.Uri
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.LyricsEntity
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.ArtistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import org.json.JSONObject
import java.io.File

object SongBundle {
    data class CoverResult(val title: String, val artist: String, val thumbUrl: String, val fullUrl: String)

    private const val COVER_DIR = "song_covers"

    suspend fun searchCovers(query: String): List<CoverResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val url = "https://itunes.apple.com/search".toHttpUrl().newBuilder()
            .addQueryParameter("term", query.trim())
            .addQueryParameter("entity", "album")
            .addQueryParameter("limit", "24")
            .build()
        runCatching {
            YTPlayerUtils.httpClient.newCall(Request.Builder().url(url).build()).execute().use { response ->
                if (!response.isSuccessful) return@use emptyList()
                val results = JSONObject(response.body.string()).optJSONArray("results") ?: return@use emptyList()
                (0 until results.length()).mapNotNull { i ->
                    val item = results.optJSONObject(i) ?: return@mapNotNull null
                    val art = item.optString("artworkUrl100").takeIf { it.isNotEmpty() } ?: return@mapNotNull null
                    CoverResult(
                        title = item.optString("collectionName"),
                        artist = item.optString("artistName"),
                        thumbUrl = art.replace("100x100bb", "300x300bb"),
                        fullUrl = art.replace("100x100bb", "1200x1200bb"),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private suspend fun saveImage(context: Context, key: String, url: String): Uri? = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, COVER_DIR).apply { mkdirs() }
        val target = File(dir, "${key.hashCode().toUInt()}.jpg")
        val tmp = File(dir, "${target.name}.part")
        val saved = runCatching {
            YTPlayerUtils.httpClient.newCall(Request.Builder().url(url).build()).execute().use { response ->
                check(response.isSuccessful) { "HTTP ${response.code}" }
                tmp.outputStream().use { out -> response.body.byteStream().copyTo(out) }
            }
            tmp.renameTo(target) || (target.delete() && tmp.renameTo(target))
        }.getOrDefault(false)
        if (!saved) tmp.delete()
        // The path never changes for a key, so cache-bust the image loader with the write time.
        if (saved) Uri.fromFile(target).buildUpon().appendQueryParameter("v", System.currentTimeMillis().toString()).build() else null
    }

    suspend fun applySongCover(context: Context, database: MusicDatabase, songId: String, url: String): Boolean {
        val uri = saveImage(context, songId, url) ?: return false
        val song = withContext(Dispatchers.IO) { database.getSongByIdBlocking(songId)?.song } ?: return false
        withContext(Dispatchers.IO) { database.upsert(song.copy(thumbnailUrl = uri.toString())) }
        return true
    }

    /** Local override of an album's art; an online refresh of the album may restore the original. */
    suspend fun applyAlbumCover(context: Context, database: MusicDatabase, albumId: String, url: String): Boolean {
        val uri = saveImage(context, "album:$albumId", url) ?: return false
        val album = withContext(Dispatchers.IO) { database.album(albumId).first()?.album } ?: return false
        withContext(Dispatchers.IO) { database.update(album.copy(thumbnailUrl = uri.toString())) }
        return true
    }

    suspend fun applyArtistCover(context: Context, database: MusicDatabase, artistId: String, url: String): Boolean {
        val uri = saveImage(context, "artist:$artistId", url) ?: return false
        val artist = withContext(Dispatchers.IO) { database.artist(artistId).first()?.artist } ?: return false
        withContext(Dispatchers.IO) { database.update(artist.copy(thumbnailUrl = uri.toString())) }
        return true
    }

    /** Saves the online artist's photo for an artist known only from device files; false when none matches by name. */
    suspend fun downloadArtistImage(context: Context, database: MusicDatabase, artistId: String, name: String): Boolean {
        val match = YouTube.search(name, SearchFilter.FILTER_ARTIST).getOrNull()?.items
            ?.filterIsInstance<ArtistItem>()
            ?.firstOrNull { it.title.equals(name, ignoreCase = true) && it.thumbnail != null }
            ?: return false
        return applyArtistCover(context, database, artistId, match.thumbnail!!.replace(Regex("=w\\d+-h\\d+.*$"), "=w1080-h1080"))
    }

    suspend fun applyLyrics(database: MusicDatabase, songId: String, lyrics: String, provider: String) {
        withContext(Dispatchers.IO) {
            database.upsert(LyricsEntity(id = songId, lyrics = lyrics, provider = provider))
        }
    }
}
