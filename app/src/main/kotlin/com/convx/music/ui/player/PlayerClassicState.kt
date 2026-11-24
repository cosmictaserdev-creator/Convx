/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableFloatState
import com.convx.music.playback.PlayerConnection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.models.MediaMetadata
import com.convx.music.ui.player.CanvasArtworkPlaybackCache
import com.convx.music.ui.player.normalizeCanvasArtistName
import com.convx.music.ui.player.normalizeCanvasSongTitle
import com.convx.music.utils.SpotifyCanvasLookup
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/** Sleep-timer UI state: whether one is running, the time left, and the dialog's own state. */
@Stable
class SleepTimerState(
    val enabled: Boolean,
    val timeLeft: State<Long>,
    val showDialog: MutableState<Boolean>,
    val value: MutableFloatState,
)

@Composable
fun rememberSleepTimerState(playerConnection: PlayerConnection): SleepTimerState {
    val sleepTimerEnabled =
        remember(
            playerConnection.service.sleepTimer.triggerTime,
            playerConnection.service.sleepTimer.pauseWhenSongEnd
        ) {
            playerConnection.service.sleepTimer.isActive
        }

    val timeLeft = remember { mutableLongStateOf(0L) }
    LaunchedEffect(sleepTimerEnabled) {
        if (sleepTimerEnabled) {
            while (isActive) {
                timeLeft.longValue =
                    if (playerConnection.service.sleepTimer.pauseWhenSongEnd) {
                        playerConnection.player.duration - playerConnection.player.currentPosition
                    } else {
                        playerConnection.service.sleepTimer.triggerTime - System.currentTimeMillis()
                    }
                delay(1000L)
            }
        }
    }

    val showDialog = remember { mutableStateOf(false) }
    val value = remember { mutableFloatStateOf(30f) }
    return SleepTimerState(sleepTimerEnabled, timeLeft, showDialog, value)
}

/** Time left to show beside the "Sleep timer" menu row, or null while no timer runs. */
val SleepTimerState.menuLabel: String?
    get() = if (enabled) com.convx.music.utils.makeTimeString(timeLeft.value.coerceAtLeast(0)) else null

/** The dialog that starts a sleep timer; renders nothing while [SleepTimerState.showDialog] is false. */
@Composable
fun SleepTimerDialogHost(playerConnection: PlayerConnection, state: SleepTimerState) {
    var showSleepTimerDialog by state.showDialog
    var sleepTimerValue by state.value
    if (showSleepTimerDialog) {
        SleepTimerDialog(
            sleepTimerValue = sleepTimerValue,
            onSleepTimerValueChange = { sleepTimerValue = it },
            onConfirm = { minutes ->
                showSleepTimerDialog = false
                playerConnection.service.sleepTimer.start(minutes)
            },
            onEndOfSong = {
                showSleepTimerDialog = false
                playerConnection.service.sleepTimer.start(-1)
            },
            onDismiss = { showSleepTimerDialog = false },
        )
    }
}

/** The classic APPLE_MUSIC background's canvas candidates (V2 fetches its own in PlayerV2Canvas.kt). */
@Stable
class ClassicCanvasState(
    val candidates: MutableState<List<CanvasArtwork>>,
    val index: MutableIntState,
)

@Composable
fun rememberClassicCanvasState(
    mediaMetadata: MediaMetadata?,
    playerBackground: PlayerBackgroundStyle,
    canvasSource: CanvasSource,
    useAppleMusicPlayer: Boolean,
    enableCanvas: Boolean,
): ClassicCanvasState {
    val context = LocalContext.current
    val canvasCandidatesState = remember(mediaMetadata?.id) { mutableStateOf<List<CanvasArtwork>>(emptyList()) }
    var canvasCandidates by canvasCandidatesState
    val canvasCandidateIndexState = remember(mediaMetadata?.id) { mutableIntStateOf(0) }
    var canvasCandidateIndex by canvasCandidateIndexState
    var canvasFetchInFlight by remember(mediaMetadata?.id) { mutableStateOf(false) }
    val canvasArtwork = canvasCandidates.getOrNull(canvasCandidateIndex)

    // useAppleMusicPlayer: this result is only drawn by classic's own APPLE_MUSIC background
    // branch below -- V2 fetches its own canvas candidates independently (PlayerV2Canvas.kt),
    // so fetching here too when V2 is the active skin is wasted network/CPU, never shown.
    LaunchedEffect(mediaMetadata?.id, playerBackground, canvasSource, useAppleMusicPlayer) {
        if (playerBackground != PlayerBackgroundStyle.APPLE_MUSIC || !enableCanvas || useAppleMusicPlayer) {
            canvasCandidates = emptyList()
            return@LaunchedEffect
        }
        val item = mediaMetadata ?: return@LaunchedEffect

        // Use cached artwork if available — it already proved it plays.
        CanvasArtworkPlaybackCache.get("${item.id}:${canvasSource.name}")?.let { cached ->
            canvasCandidates = listOf(cached)
            canvasCandidateIndex = 0
            return@LaunchedEffect
        }

        if (canvasFetchInFlight) return@LaunchedEffect
        canvasFetchInFlight = true

        withContext(Dispatchers.IO) {
            val storefront = Locale.getDefault().country.lowercase(Locale.ROOT).takeIf { it.length == 2 } ?: "us"
            val requestedTitle = item.title
            val requestedArtist = item.artists.joinToString { it.name }
            val requestedAlbum = item.album?.title ?: ""

            val s = normalizeCanvasSongTitle(requestedTitle)
            val a = normalizeCanvasArtistName(requestedArtist)

            val candidates = when (canvasSource) {
                CanvasSource.AUTO -> {
                    val spotify = SpotifyCanvasLookup.getBySongArtist(context, requestedTitle, requestedArtist)
                    val appleAlbum = if (requestedAlbum.isNotBlank()) {
                        AppleMusicCanvasProvider.getByAlbumArtist(
                            album = requestedAlbum,
                            artist = a,
                            storefront = storefront
                        )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    } else null
                    val appleSong = AppleMusicCanvasProvider.getBySongArtist(s, a, requestedAlbum, storefront)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    val convx = ConvxCanvasProvider.getBySongArtist(s, a)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    val tidal = TidalCanvasProvider.getBySongArtist(s, a, requestedAlbum)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                    listOfNotNull(spotify, appleAlbum, appleSong, convx, tidal)
                }
                CanvasSource.SPOTIFY -> listOfNotNull(
                    SpotifyCanvasLookup.getBySongArtist(context, requestedTitle, requestedArtist)
                )
                CanvasSource.APPLE_MUSIC -> listOfNotNull(
                    AppleMusicCanvasProvider.getBySongArtist(s, a, requestedAlbum, storefront)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                )
                CanvasSource.CONVX -> listOfNotNull(
                    ConvxCanvasProvider.getBySongArtist(s, a)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                )
                CanvasSource.TIDAL -> listOfNotNull(
                    TidalCanvasProvider.getBySongArtist(s, a, requestedAlbum)
                        ?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                )
            }

            val validated = candidates.filter { artwork ->
                val resultArtist = artwork.artist
                if (resultArtist != null && requestedArtist.isNotBlank()) {
                    resultArtist.contains(requestedArtist, ignoreCase = true) ||
                    requestedArtist.contains(resultArtist, ignoreCase = true)
                } else true
            }

            withContext(Dispatchers.Main) {
                canvasCandidates = validated
                canvasCandidateIndex = 0
                canvasFetchInFlight = false
            }
        }
    }
    return ClassicCanvasState(canvasCandidatesState, canvasCandidateIndexState)
}
