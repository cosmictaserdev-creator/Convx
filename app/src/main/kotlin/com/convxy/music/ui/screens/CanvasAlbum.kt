/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.convxy.music.applecanvas.AppleMusicCanvasProvider
import com.convxy.music.canvas.CanvasArtwork
import com.convxy.music.canvas.TidalCanvasProvider
import com.convxy.music.ui.player.CanvasArtworkPlaybackCache
import com.convxy.music.vivimusiccanvas.EchoMusicCanvasProvider
import com.convxy.music.vivimusiccanvas.ViviMusicCanvasProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/** [artwork] is the candidate currently being tried; call [onError] when its video
 * fails to actually play (a match at fetch time doesn't guarantee the link still
 * works) to advance to the next one, and [onReady] once it's confirmed playing so
 * it gets cached as the known-good pick for next time. */
class AlbumCanvasState(
    val artwork: CanvasArtwork?,
    val onError: () -> Unit,
    val onReady: () -> Unit,
)

/**
 * Album hero canvas, same priority order and dead-link fallback as the full
 * player background (see Player.kt): Echo/Vivi are song-keyed so they're
 * queried with [firstSongTitle] and validated by artist only (they don't
 * report an album name); Apple/Tidal are queried by album and validated
 * strictly on both artist and album.
 */
@Composable
fun rememberAlbumCanvas(
    albumTitle: String?,
    artistName: String?,
    firstSongTitle: String? = null,
): AlbumCanvasState {
    val cacheKey = remember(albumTitle, artistName) {
        if (albumTitle != null && artistName != null) "album|$albumTitle|$artistName" else null
    }

    var candidates by remember(cacheKey) {
        mutableStateOf(cacheKey?.let { CanvasArtworkPlaybackCache.get(it) }?.let { listOf(it) } ?: emptyList())
    }
    var candidateIndex by remember(cacheKey) { mutableIntStateOf(0) }

    val storefront = remember {
        val country = Locale.getDefault().country
        if (country.length == 2) country.lowercase(Locale.ROOT) else "us"
    }

    LaunchedEffect(albumTitle, artistName, firstSongTitle) {
        if (candidates.isNotEmpty() || cacheKey == null) return@LaunchedEffect
        if (albumTitle.isNullOrBlank() || artistName.isNullOrBlank()) return@LaunchedEffect

        val fetched = withContext(Dispatchers.IO) {
            val songQuery = firstSongTitle?.takeIf { it.isNotBlank() } ?: albumTitle

            val echo = EchoMusicCanvasProvider.getBySongArtist(songQuery, artistName)
                ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                ?.takeIf { artistOnlyMatches(it.artist, artistName) }

            val vivi = ViviMusicCanvasProvider.getBySongArtist(songQuery, artistName)
                ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                ?.takeIf { artistOnlyMatches(it.artist, artistName) }

            val apple = AppleMusicCanvasProvider.getByAlbumArtist(
                album = albumTitle,
                artist = artistName,
                storefront = storefront,
            )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                ?.takeIf { artistAndAlbumMatch(it, albumTitle, artistName) }

            val tidal = TidalCanvasProvider.getByAlbumArtist(
                album = albumTitle,
                artist = artistName,
            )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                ?.takeIf { artistAndAlbumMatch(it, albumTitle, artistName) }

            listOfNotNull(echo, apple, vivi, tidal)
        }

        candidates = fetched
        candidateIndex = 0
    }

    return AlbumCanvasState(
        artwork = candidates.getOrNull(candidateIndex),
        onError = {
            candidateIndex = if (candidateIndex < candidates.lastIndex) candidateIndex + 1 else candidates.size
        },
        onReady = {
            candidates.getOrNull(candidateIndex)?.let { cacheKey?.let { key -> CanvasArtworkPlaybackCache.put(key, it) } }
        },
    )
}

private fun artistOnlyMatches(resultArtist: String?, requestedArtist: String): Boolean {
    if (resultArtist == null || requestedArtist.isBlank()) return true
    val requestedList = splitAndNormalizeArtists(requestedArtist)
    val resultList = splitAndNormalizeArtists(resultArtist)
    return requestedList.any { req -> resultList.any { res -> res.contains(req) || req.contains(res) } }
}

private fun artistAndAlbumMatch(artwork: CanvasArtwork, albumTitle: String, artistName: String): Boolean {
    val albumMatches = artwork.albumName?.trim()?.equals(albumTitle.trim(), ignoreCase = true) ?: false
    return albumMatches && artistOnlyMatches(artwork.artist, artistName)
}

private fun splitAndNormalizeArtists(raw: String): List<String> {
    return raw.split(
        Regex(
            "(?:\\s*,\\s*|\\s*&\\s*|\\s+×\\s+|\\s+x\\s+|\\bfeat\\.?\\b|\\bft\\.?\\b|\\bfeaturing\\b|\\bwith\\b)",
            RegexOption.IGNORE_CASE,
        )
    ).map { it.replace(Regex("\\s+"), " ").trim().lowercase(Locale.ROOT) }
        .filter { it.isNotBlank() }
}
