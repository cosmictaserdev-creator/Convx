/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.ambient

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.ui.AspectRatioFrameLayout
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.constants.AmbientCanvasFitMode
import com.convx.music.constants.CanvasSource
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.models.MediaMetadata
import com.convx.music.ui.player.CanvasArtworkPlaybackCache
import com.convx.music.ui.player.CanvasArtworkPlayer
import com.convx.music.utils.SpotifyCanvasLookup
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/** Resolves and renders a canvas without changing the main player's canvas settings. */
@Composable
fun AmbientVideoCanvas(
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    canvasSource: CanvasSource,
    modifier: Modifier = Modifier,
    fitMode: AmbientCanvasFitMode = AmbientCanvasFitMode.ZOOM,
    onReady: () -> Unit = {},
    onExhausted: () -> Unit = {},
    onVideoAspectRatio: (Float) -> Unit = {},
) {
    val context = LocalContext.current
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
        Locale.getDefault().country.takeIf { it.length == 2 }?.lowercase(Locale.ROOT) ?: "us"
    }

    LaunchedEffect(mediaMetadata.id, albumTitle, canvasSource) {
        canvasCandidates = emptyList()
        canvasCandidateIndex = 0
        CanvasArtworkPlaybackCache.get(cacheKey)?.let { cached ->
            canvasCandidates = listOf(cached)
            return@LaunchedEffect
        }

        val title = mediaMetadata.title
        val artist = mediaMetadata.artists.joinToString(", ") { it.name }
        val album = albumTitle.orEmpty()
        canvasCandidates = withContext(Dispatchers.IO) {
            try {
                suspend fun appleCandidates(): List<CanvasArtwork> {
                    val albumMatch = if (album.isBlank()) null else {
                        AppleMusicCanvasProvider.getByAlbumArtist(album, artist, storefront)
                    }
                    val songMatch = AppleMusicCanvasProvider.getBySongArtist(title, artist, album, storefront)
                    return listOfNotNull(albumMatch, songMatch)
                        .filter { !it.preferredAnimationUrl.isNullOrBlank() }
                }

                when (canvasSource) {
                    CanvasSource.AUTO -> {
                        val spotify = SpotifyCanvasLookup.getBySongArtist(context, title, artist)
                        val apple = appleCandidates()
                        val convx = ConvxCanvasProvider.getBySongArtist(title, artist, album)
                        val tidal = TidalCanvasProvider.getBySongArtist(title, artist, album)
                        listOfNotNull(spotify) + apple + listOfNotNull(convx, tidal)
                            .filter { !it.preferredAnimationUrl.isNullOrBlank() }
                    }
                    CanvasSource.SPOTIFY -> listOfNotNull(
                        SpotifyCanvasLookup.getBySongArtist(context, title, artist)
                    )
                    CanvasSource.APPLE_MUSIC -> appleCandidates()
                    CanvasSource.CONVX -> listOfNotNull(
                        ConvxCanvasProvider.getBySongArtist(title, artist, album)
                            ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    )
                    CanvasSource.TIDAL -> listOfNotNull(
                        TidalCanvasProvider.getBySongArtist(title, artist, album)
                            ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    val artwork = canvasCandidates.getOrNull(canvasCandidateIndex)
    artwork?.let { candidate ->
        CanvasArtworkPlayer(
            primaryUrl = candidate.animated,
            fallbackUrl = candidate.videoUrl,
            isPlaying = isPlaying,
            mediaId = mediaMetadata.id,
            resizeMode = when (fitMode) {
                AmbientCanvasFitMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                AmbientCanvasFitMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                AmbientCanvasFitMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            },
            onVideoAspectRatio = onVideoAspectRatio,
            onReady = {
                CanvasArtworkPlaybackCache.put(cacheKey, candidate)
                onReady()
            },
            onExhausted = {
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
