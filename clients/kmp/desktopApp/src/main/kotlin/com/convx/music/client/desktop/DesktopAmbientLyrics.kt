package com.convx.music.client.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.catalog.presentation.RemoteArtwork
import com.convx.music.playback.PlaybackSnapshot
import com.convx.music.playback.LyricsAnimationStyle
import com.convx.music.playback.shouldPauseOnToggle
import com.convx.music.playback.presentation.*
import com.convx.music.ui.ios.foundation.ProvideIosTheme
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.darkIosColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Fills the main window; a second Swing window cannot be created from the Tao-hosted application. */
@Composable
internal fun DesktopAmbientLyricsWindow(controller: DesktopMusicController, lyrics: LyricsStateHolder,
    settings: PlaybackSettingsStateHolder, onDismiss: () -> Unit) {
    val preferences by settings.state.collectAsState()
    val typography = IosTheme.typography
    ProvideIosTheme(darkIosColors(), typography) {
        DesktopNucleusTheme(glass = false, reducedMotion = preferences.reducedMotion) {
            DesktopAmbientLyrics(controller, lyrics, settings, onDismiss, Modifier.fillMaxSize())
        }
    }
}

@Composable
internal fun DesktopAmbientLyrics(controller: DesktopMusicController, lyrics: LyricsStateHolder,
    settings: PlaybackSettingsStateHolder, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val preferences by settings.state.collectAsState()
    val playback by remember(controller) { controller.playbackSnapshot.map { it.copy(positionMs = 0L) }.distinctUntilChanged() }
        .collectAsState(PlaybackSnapshot.EMPTY)
    val position = rememberPlaybackPosition(controller.playbackSnapshot)
    var interaction by remember { mutableIntStateOf(0) }
    var controls by remember { mutableStateOf(true) }
    LaunchedEffect(interaction) { controls = true; delay(4_000); controls = false }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    Box(modifier.background(Color(0xFF101014)).focusRequester(focus).onKeyEvent {
        if (it.type != KeyEventType.KeyDown) false else when (it.key) {
            Key.Escape -> { onDismiss(); true }
            Key.Spacebar -> { controller.togglePlayPause(); interaction++; true }
            else -> false
        }
    }.focusable().pointerInput(Unit) {
        awaitPointerEventScope { while (true) {
            val event = awaitPointerEvent()
            if (event.type == PointerEventType.Move || event.type == PointerEventType.Enter || event.type == PointerEventType.Press || event.type == PointerEventType.Scroll) interaction++
        } }
    }) {
        RemoteArtwork(playback.track?.artworkUrl, Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = .82f), Color.Black.copy(alpha = .9f)))))
        Column(Modifier.fillMaxSize().padding(horizontal = 36.dp, vertical = 20.dp)) {
            Box(Modifier.height(52.dp).fillMaxWidth()) {
            androidx.compose.animation.AnimatedVisibility(controls, enter = fadeIn(tween(if (preferences.reducedMotion) 0 else 120)), exit = fadeOut(tween(if (preferences.reducedMotion) 0 else 180))) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(playback.track?.title ?: "Ambient lyrics", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(playback.track?.artists?.joinToString().orEmpty(), color = Color.White.copy(alpha = .6f), fontSize = 13.sp,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    dev.nucleusframework.macoscompose.components.PushButton(
                        if (preferences.lyricsAnimationStyle == LyricsAnimationStyle.FISHEYE) "Synced view" else "Fisheye",
                        { settings.setLyricsAnimationStyle(if (preferences.lyricsAnimationStyle == LyricsAnimationStyle.FISHEYE) LyricsAnimationStyle.FLAMINGO else LyricsAnimationStyle.FISHEYE); interaction++ })
                    DesktopIconButton(if (playback.shouldPauseOnToggle()) DesktopGlyphKind.PAUSE else DesktopGlyphKind.PLAY,
                        if (playback.shouldPauseOnToggle()) "Pause" else "Play", onClick = { controller.togglePlayPause(); interaction++ })
                    DesktopIconButton(DesktopGlyphKind.COLLAPSE, "Exit ambient mode", onClick = onDismiss)
                }
            }
            }
            PlaybackLyrics(lyrics, { lyricDisplayPosition(position.longValue, preferences.lyricsOffsetMs) }, controller::seekTo,
                Modifier.weight(1f).widthIn(max = 1_200.dp).fillMaxWidth().align(Alignment.CenterHorizontally), textColor = Color.White,
                fontSize = (preferences.lyricsFontSize * 1.5f).coerceAtMost(64f), animationsEnabled = !preferences.reducedMotion,
                animationStyle = preferences.lyricsAnimationStyle, offsetMs = preferences.lyricsOffsetMs,
                showToolbar = controls,
                providersEnabled = !preferences.localMode, onProviderSelected = settings::setLyricsProvider)
        }
    }
}
