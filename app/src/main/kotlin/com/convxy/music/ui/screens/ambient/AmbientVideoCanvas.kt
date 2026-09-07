/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.ambient

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.ui.AspectRatioFrameLayout
import com.convxy.music.applecanvas.AppleMusicCanvasProvider
import com.convxy.music.canvas.CanvasArtwork
import com.convxy.music.canvas.TidalCanvasProvider
import com.convxy.music.constants.AmbientCanvasFitMode
import com.convxy.music.constants.CanvasSource
import com.convxy.music.models.MediaMetadata
import com.convxy.music.ui.player.CanvasArtworkPlaybackCache
import com.convxy.music.ui.player.CanvasArtworkPlayer
import com.convxy.music.vivimusiccanvas.EchoMusicCanvasProvider
import com.convxy.music.vivimusiccanvas.ViviMusicCanvasProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Resolves and renders the current track's canvas using the same providers, cache, and
 * muted canvas player used by the main player. The setting that gates this composable
 * is Ambient-specific, so it can be enabled without changing the main player's canvas.
 *
 * The candidate list mirrors the main player's source priority. Keeping the list means
 * a stale URL from one provider can fall through to another provider at playback time,
 * rather than leaving a black ambient background.
 *
 * [fitMode] and [onVideoAspectRatio] exist for Canvas Position & Fit: the screen picks the
 * panel's width from the reported frame geometry, and asks for a fit that shows the whole
 * frame inside it. Both keep the full-screen, crop-to-fill behaviour as their default, so a
 * caller that has no opinion gets Ambient Mode's original background.
 */
@Composable
fun AmbientVideoCanvas(
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    canvasSource: CanvasSource,
    modifier: Modifier = Modifier,
    // ZOOM keeps Ambient Mode's historical full-screen behaviour, where the canvas is
    // cropped until it covers everything. Position & Fit switches to FIT so a portrait
    // canvas is shown whole inside its side panel.
    fitMode: AmbientCanvasFitMode = AmbientCanvasFitMode.ZOOM,
    onReady: () -> Unit = {},
    onExhausted: () -> Unit = {},
    onVideoAspectRatio: (Float) -> Unit = {},
) {
    if (mediaMetadata == null) return

    val albumTitle = mediaMetadata.album?.title
    val cacheKey = "${mediaMetadata.id}:${canvasSource.name}"
    var canvasCandidates by remember(mediaMetadata.id, albumTitle, canvasSource) {
        mutableStateOf<List<CanvasArtwork>>(emptyList())
    }
    var canvasCandidateIndex by remember(mediaMetadata.id, albumTitle, canvasSource) {
        mutableIntStateOf(0)
    }

    val storefront = remember {
        val country = Locale.getDefault().country
        if (country.length == 2) country.lowercase(Locale.ROOT) else "us"
    }

    LaunchedEffect(mediaMetadata.id, albumTitle, canvasSource) {
        CanvasArtworkPlaybackCache.get(cacheKey)?.let { cached ->
            canvasCandidates = listOf(cached)
            canvasCandidateIndex = 0
            return@LaunchedEffect
        }

        val songTitle = mediaMetadata.title
        val artistName = mediaMetadata.artists.joinToString(", ") { it.name }
        val albumName = albumTitle.orEmpty()
        val fetched = withContext(Dispatchers.IO) {
            when (canvasSource) {
                CanvasSource.AUTO -> {
                    val echo = EchoMusicCanvasProvider.getBySongArtist(songTitle, artistName)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    val appleAlbum = if (albumName.isNotBlank()) {
                        AppleMusicCanvasProvider.getByAlbumArtist(
                            album = albumName,
                            artist = artistName,
                            storefront = storefront,
                        )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    } else {
                        null
                    }
                    val appleSong = AppleMusicCanvasProvider.getBySongArtist(
                        song = songTitle,
                        artist = artistName,
                        album = albumName,
                        storefront = storefront,
                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    val vivi = ViviMusicCanvasProvider.getBySongArtist(songTitle, artistName)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    val tidal = TidalCanvasProvider.getBySongArtist(songTitle, artistName, albumName)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    listOfNotNull(echo, appleAlbum, appleSong, vivi, tidal)
                }

                CanvasSource.ECHO_MUSIC -> listOfNotNull(
                    EchoMusicCanvasProvider.getBySongArtist(songTitle, artistName)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                )

                CanvasSource.APPLE_MUSIC -> listOfNotNull(
                    AppleMusicCanvasProvider.getBySongArtist(
                        song = songTitle,
                        artist = artistName,
                        album = albumName,
                        storefront = storefront,
                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                )

                CanvasSource.VIVIMUSIC -> listOfNotNull(
                    ViviMusicCanvasProvider.getBySongArtist(songTitle, artistName)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                )

                CanvasSource.TIDAL -> listOfNotNull(
                    TidalCanvasProvider.getBySongArtist(songTitle, artistName, albumName)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                )
            }
        }

        canvasCandidates = fetched
        canvasCandidateIndex = 0
    }

    val artwork = canvasCandidates.getOrNull(canvasCandidateIndex)
    artwork?.let { candidate ->
        CanvasArtworkPlayer(
            primaryUrl = candidate.animated,
            fallbackUrl = candidate.videoUrl,
            isPlaying = isPlaying,
            mediaId = mediaMetadata.id,
            resizeMode = when (fitMode) {
                // RESIZE_MODE_FILL is Media3's "ignore the aspect ratio" mode, i.e. stretch.
                AmbientCanvasFitMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                AmbientCanvasFitMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                AmbientCanvasFitMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            },
            onVideoAspectRatio = onVideoAspectRatio,
            onReady = {
                // Only cache a URL after the shared player has rendered its first frame.
                CanvasArtworkPlaybackCache.put(cacheKey, candidate)
                onReady()
            },
            onExhausted = {
                // Let Ambient Mode reveal its lightweight glow fallback during a retry.
                onExhausted()
                if (canvasCandidateIndex < canvasCandidates.lastIndex) {
                    canvasCandidateIndex++
                } else {
                    canvasCandidates = emptyList()
                }
            },
            modifier = modifier,
        )
    }
}
