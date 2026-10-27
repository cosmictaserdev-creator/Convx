package com.convx.music.client.desktop

import dev.nucleusframework.macoscompose.components.IconButton as NucleusIconButton
import dev.nucleusframework.macoscompose.components.IconButtonStyle
import dev.nucleusframework.macoscompose.components.PushButton
import dev.nucleusframework.macoscompose.components.PushButtonStyle
import dev.nucleusframework.macoscompose.components.Slider as NucleusSlider
import dev.nucleusframework.macoscompose.components.SliderDefaults
import dev.nucleusframework.macoscompose.theme.ControlSize

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.ColorFilter
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.CancellationException
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.catalog.presentation.RemoteArtwork
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.playback.PlaybackSnapshot
import com.convx.music.playback.PlayerStyle
import com.convx.music.playback.PlayerSection
import com.convx.music.playback.presentation.PlaybackSeekSlider
import com.convx.music.playback.presentation.PlaybackSharedElements
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import org.koin.compose.koinInject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
internal fun DesktopExpandedPlayer(
    controller: DesktopMusicController,
    shared: PlaybackSharedElements,
    crossfade: Boolean,
    autoMix: Boolean,
    sleepTimer: Boolean,
    onCrossfade: () -> Unit,
    onAutoMix: () -> Unit,
    onEqualizer: () -> Unit,
    onSleepTimer: () -> Unit,
    onQueue: () -> Unit,
    onCollapse: () -> Unit,
    headerModifier: Modifier,
    modifier: Modifier = Modifier,
    editor: DesktopDiyStateHolder? = null,
    onLyrics: (() -> Unit)? = null,
    liked: Boolean = false,
    likeEnabled: Boolean = false,
    onToggleLike: (() -> Unit)? = null,
) {
    val appearanceSettings: PlaybackSettingsStateHolder = koinInject()
    val appearancePreferences by appearanceSettings.state.collectAsState()
    val artworkShape = RoundedCornerShape(appearancePreferences.appearance.artworkCornerRadius.dp)
    val customization = appearancePreferences.playerCustomization
    val assets: DesktopPlayerAssets = koinInject()
    val editorState = editor?.state?.collectAsState()?.value
    val playback by remember(controller) {
        controller.playbackSnapshot.map { it.copy(positionMs = 0L) }.distinctUntilChanged()
    }.collectAsState(PlaybackSnapshot.EMPTY)
    val track = playback.track
    val captionWidth = dev.nucleusframework.macoscompose.components.LocalWindowControlTrailingInset.current
        .let { if (it == androidx.compose.ui.unit.Dp.Unspecified) 0.dp else it }
    val artworkColor by produceState<Int?>(null, track?.artworkUrl) {
        value = try { DesktopArtworkColors.estimate(track?.artworkUrl) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { null }
    }
    val artworkScale by animateFloatAsState(if (playback.isPlaying || editor != null) 1f else .94f,
        tween(if (appearancePreferences.reducedMotion) 0 else 260), label = "Player artwork playback state")
    val focus = remember { FocusRequester() }
    LaunchedEffect(editor) { if (editor == null) focus.requestFocus() }
    DesktopArtistTheme(true, DesktopDetailArtwork(backgroundRgb = artworkColor)) {
    val palette = desktopPalette()
    BoxWithConstraints(modifier.background(palette.background).focusRequester(focus).onPreviewKeyEvent {
        if (editor != null || it.type != KeyEventType.KeyDown) false else when {
            it.key == Key.Spacebar -> { controller.togglePlayPause(); true }
            it.key == Key.Escape -> { onCollapse(); true }
            it.isCtrlPressed && it.key == Key.DirectionRight -> { controller.requestSkip(true); true }
            it.isCtrlPressed && it.key == Key.DirectionLeft -> { controller.requestSkip(false); true }
            else -> false
        }
    }.focusable()) {
        val wide = maxWidth >= 880.dp && customization.style == PlayerStyle.WIDE
        val landscape = maxWidth > maxHeight
        val referenceLayout = !wide && editor == null && customization.order == PlayerSection.entries.toList()
        val coverSize = minOf(if (wide) maxWidth * .43f else maxWidth * .56f,
            if (referenceLayout) (maxHeight - 320.dp).coerceAtLeast(180.dp) else maxHeight * if (wide) .74f else .62f, 600.dp)
        RemoteArtwork(track?.artworkUrl, Modifier.fillMaxSize().blur(110.dp).graphicsLayer { alpha = .24f })
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(
            palette.background.copy(alpha = .2f), palette.background.copy(alpha = .65f)))))
        Column(Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = 14.dp)) {
            Row(Modifier.fillMaxWidth().padding(end = captionWidth)
                .then(dev.nucleusframework.macoscompose.components.LocalNoWindowDragModifier.current), verticalAlignment = Alignment.CenterVertically) {
                PlayerGlyphButton(DesktopGlyphKind.COLLAPSE, "Collapse player", onClick = onCollapse)
                Spacer(Modifier.weight(1f))
                if (!referenceLayout) Text("NOW PLAYING", color = palette.muted, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.4.sp)
                Spacer(Modifier.weight(1f))
                onLyrics?.let { PlayerGlyphButton(DesktopGlyphKind.LYRICS, "Lyrics", onClick = it) }
                PlayerGlyphButton(DesktopGlyphKind.QUEUE, "Queue", onClick = onQueue)
            }
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
                val protected = remember { mutableStateMapOf<PlayerSection, Rect>() }
                val sectionBounds: (PlayerSection) -> Modifier = { section -> Modifier.onGloballyPositioned { child ->
                    coordinates?.takeIf { it.isAttached && child.isAttached }?.let { protected[section] = it.localBoundingBoxOf(child, clipBounds = false) }
                } }
                Box(Modifier.fillMaxSize().onGloballyPositioned { coordinates = it })
                DesktopDiyLayer(customization.diy.stickers.filter { it.z < 0 }, assets, landscape, editorState?.selectedId, editor)
                val artwork: @Composable () -> Unit = {
                    Box(shared.artwork.size(coverSize).graphicsLayer { scaleX = artworkScale; scaleY = artworkScale }.shadow(32.dp, artworkShape)
                        .clip(artworkShape).background(palette.surface)) {
                        RemoteArtwork(track?.artworkUrl, Modifier.fillMaxSize())
                    }
                }
                val section: @Composable (PlayerSection) -> Unit = { part ->
                    if (part !in customization.hidden) when (part) {
                        PlayerSection.ALBUM_ART -> artwork()
                        PlayerSection.TRACK_INFO -> Row(sectionBounds(part), verticalAlignment = Alignment.CenterVertically) {
                          Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(if (referenceLayout) 3.dp else 8.dp)) {
                            Text(track?.title ?: "Nothing Playing", modifier = shared.title, fontSize = if (wide) 28.sp else 18.sp,
                                lineHeight = if (wide) 34.sp else 23.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, letterSpacing = (-0.6).sp)
                            Text(track?.artists?.joinToString().orEmpty(), modifier = shared.artist, color = palette.muted,
                                fontSize = if (wide) 20.sp else 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                          }
                          if (referenceLayout) {
                            onToggleLike?.let { action ->
                                PlayerGlyphButton(if (liked) DesktopGlyphKind.FAVORITED else DesktopGlyphKind.FAVORITE,
                                    if (liked) "Remove from favourites" else "Add to favourites", liked, size = 32, icon = 17,
                                    enabled = likeEnabled, onClick = action)
                            }
                            Box {
                                var menu by remember { mutableStateOf(false) }
                                PlayerGlyphButton(DesktopGlyphKind.MORE, "Playback options", size = 32, icon = 17, onClick = { menu = true })
                                dev.nucleusframework.macoscompose.components.DropdownMenu(menu, { menu = false }) {
                                    DesktopDownloadAction(track?.let { com.convx.music.playback.PlaybackRequestTrack(it.providerId, it.id, it.title, it.artists, playback.durationMs, it.artworkUrl) },
                                        menuItem = true, onAction = { menu = false })
                                    dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { onCrossfade(); menu = false }) { Text(if (crossfade) "Turn Off Crossfade" else "Crossfade") }
                                    dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { onAutoMix(); menu = false }) { Text(if (autoMix) "Turn Off AutoMix" else "AutoMix") }
                                    dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { onEqualizer(); menu = false }) { Text("Equalizer") }
                                    dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { onSleepTimer(); menu = false }) { Text("Sleep Timer") }
                                }
                            }
                          }
                        }
                        PlayerSection.SEEK_BAR -> Box(sectionBounds(part)) { DesktopPlayerProgress(controller) }
                        PlayerSection.CONTROLS -> Column(sectionBounds(part), verticalArrangement = Arrangement.spacedBy(if (referenceLayout) 10.dp else 18.dp)) {
                          Row(Modifier.widthIn(max = if (referenceLayout) 315.dp else 800.dp).fillMaxWidth().align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            PlayerGlyphButton(DesktopGlyphKind.SHUFFLE, if (playback.shuffleEnabled) "Turn shuffle off" else "Turn shuffle on", playback.shuffleEnabled, onClick = controller::toggleShuffle)
                            PlayerGlyphButton(DesktopGlyphKind.PREVIOUS, "Previous track", size = 54, icon = 29, enabled = playback.canSkipPrevious,
                                onClick = { controller.requestSkip(false) })
                            PlayerGlyphButton(if (playback.isPlaying || playback.playWhenReady) DesktopGlyphKind.PAUSE else DesktopGlyphKind.PLAY,
                                if (playback.isPlaying) "Pause" else "Play", size = if (referenceLayout) 56 else 76, icon = if (referenceLayout) 32 else 38, enabled = track != null,
                                onClick = controller::togglePlayPause)
                            PlayerGlyphButton(DesktopGlyphKind.NEXT, "Next track", size = 54, icon = 29, enabled = playback.canSkipNext,
                                onClick = { controller.requestSkip(true) })
                            PlayerGlyphButton(if (playback.repeatMode == PlaybackRepeatMode.ONE) DesktopGlyphKind.REPEAT_ONE else DesktopGlyphKind.REPEAT,
                                "Repeat: ${playback.repeatMode.name.lowercase()}", playback.repeatMode != PlaybackRepeatMode.OFF, onClick = controller::cycleRepeatMode)
                        }
                        DesktopPlayerVolume(controller, compact = referenceLayout)
                        }
                        PlayerSection.ACTION_ROW -> Row(Modifier.fillMaxWidth().then(sectionBounds(part)), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            DesktopDownloadAction(track?.let { com.convx.music.playback.PlaybackRequestTrack(it.providerId, it.id, it.title, it.artists, playback.durationMs, it.artworkUrl) }, size = 40)
                            Spacer(Modifier.width(18.dp))
                            PlayerGlyphButton(DesktopGlyphKind.CROSSFADE, "Crossfade", crossfade, onClick = onCrossfade)
                            Spacer(Modifier.width(18.dp))
                            PlayerGlyphButton(DesktopGlyphKind.AUTOMIX, "AutoMix", autoMix, onClick = onAutoMix)
                            Spacer(Modifier.width(18.dp))
                            PlayerGlyphButton(DesktopGlyphKind.EQUALIZER, "Equalizer", onClick = onEqualizer)
                            Spacer(Modifier.width(18.dp))
                            PlayerGlyphButton(DesktopGlyphKind.TIMER, "Sleep timer", sleepTimer, onClick = onSleepTimer)
                        }
                    }
                }
                if (wide) Row(Modifier.widthIn(max = 1240.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(72.dp), verticalAlignment = Alignment.CenterVertically) {
                    section(PlayerSection.ALBUM_ART)
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(26.dp)) {
                        customization.order.filter { it != PlayerSection.ALBUM_ART }.forEach { section(it) }
                        playback.errorMessage?.let { Text(it, color = palette.muted, fontSize = 13.sp) }
                    }
                } else Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState()).padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(if (referenceLayout) 12.dp else 18.dp)) {
                    customization.order.filter { !referenceLayout || it != PlayerSection.ACTION_ROW }.forEach { part ->
                        if (part == PlayerSection.ALBUM_ART) section(part)
                        else Box(Modifier.width(coverSize)) { section(part) }
                    }
                }
                DesktopDiyLayer(customization.diy.stickers.filter { it.z >= 0 }, assets, landscape, editorState?.selectedId, editor, modifier = Modifier.drawWithContent {
                    if (editor != null) { drawContent(); return@drawWithContent }
                    val mask = Path().apply {
                        fillType = PathFillType.EvenOdd
                        addRect(Rect(0f, 0f, size.width, size.height))
                        protected.filterKeys { it !in customization.hidden }.values.forEach { addRect(it) }
                    }
                    clipPath(mask) { this@drawWithContent.drawContent() }
                })
            }
        }
    }
}
}

@Composable
internal fun DesktopPlayerProgress(controller: DesktopMusicController, compact: Boolean = false, reducedMotion: Boolean = false) {
    val palette = desktopPalette()
    val playback by controller.playbackSnapshot.collectAsState()
    var scrubbing by remember(playback.track?.providerId, playback.track?.id) { mutableStateOf(false) }
    var value by remember(playback.track?.providerId, playback.track?.id) { mutableFloatStateOf(playback.positionMs.toFloat()) }
    LaunchedEffect(playback.positionMs) { if (!scrubbing) value = playback.positionMs.toFloat() }
    val duration = playback.durationMs?.takeIf { it > 0L }
    val displayedValue = if (duration == null) 0f else value.coerceIn(0f, duration.toFloat())
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    val emphasized = hovered || focused || scrubbing
    val trackScale = animateFloatAsState(if (emphasized) 1.65f else 1f,
        tween(if (reducedMotion) 0 else 140), label = "Mini player seek hover")
    fun seek(target: Float): Boolean {
        if (duration == null) return false
        value = target.coerceIn(0f, duration.toFloat())
        controller.seekTo(value.toLong())
        return true
    }
    Column(Modifier.fillMaxWidth().then(if (compact) Modifier.height(8.dp).hoverable(interaction) else Modifier),
        verticalArrangement = Arrangement.spacedBy(if (compact) 0.dp else 1.dp)) {
        key(playback.track?.providerId, playback.track?.id) {
        NucleusSlider(
            value = displayedValue,
            valueRange = 0f..(duration?.toFloat() ?: 1f), enabled = duration != null,
            onValueChange = { scrubbing = true; value = it },
            onValueChangeFinished = { if (scrubbing) controller.seekTo(value.toLong()); scrubbing = false },
            colors = SliderDefaults.colors(
                activeTrackColor = palette.foreground.copy(alpha = .85f), thumbColor = palette.foreground,
                inactiveTrackColor = palette.foreground.copy(alpha = .16f)),
            interactionSource = interaction,
            thumb = if (compact) ({ }) else null,
            track = if (compact) ({ fraction ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.fillMaxWidth().height(3.dp).graphicsLayer { scaleY = trackScale.value }) {
                        val radius = CornerRadius(size.height / 2)
                        drawRoundRect(palette.foreground.copy(alpha = .18f), cornerRadius = radius)
                        if (fraction > 0) drawRoundRect(palette.foreground.copy(alpha = .85f),
                            size = Size(size.width * fraction, size.height), cornerRadius = radius)
                        if (emphasized && duration != null) {
                            val thumbRadius = 2.5.dp.toPx()
                            drawCircle(palette.foreground, thumbRadius,
                                Offset((size.width * fraction).coerceIn(thumbRadius, maxOf(thumbRadius, size.width - thumbRadius)), size.height / 2))
                        }
                    }
                }
            }) else null,
            modifier = Modifier.fillMaxWidth().height(if (compact) 10.dp else 28.dp)
                .semantics {
                    contentDescription = "Playback position"
                    progressBarRangeInfo = ProgressBarRangeInfo(displayedValue, 0f..(duration?.toFloat() ?: 1f))
                    setProgress { seek(it) }
                }
                .onPreviewKeyEvent {
                    if (it.type != KeyEventType.KeyDown || duration == null) false else when (it.key) {
                        Key.DirectionLeft -> seek(displayedValue - 5_000f)
                        Key.DirectionRight -> seek(displayedValue + 5_000f)
                        Key.MoveHome -> seek(0f)
                        Key.MoveEnd -> seek(duration.toFloat())
                        else -> false
                    }
                }.focusable(duration != null, interaction),
        )
        }
        if (!compact) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(playerTime(value.toLong()), color = palette.muted, fontSize = 11.sp)
            Text(duration?.let { "−${playerTime((it - value.toLong()).coerceAtLeast(0))}" } ?: "—", color = palette.muted, fontSize = 11.sp)
        }
    }
}

@Composable
internal fun DesktopPlayerVolume(controller: DesktopMusicController, compact: Boolean = false) {
    val palette = desktopPalette()
    val settings: PlaybackSettingsStateHolder = koinInject()
    val preferences by settings.state.collectAsState()
    var value by remember(preferences.volume) { mutableFloatStateOf(preferences.volume) }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        PlayerGlyphButton(if (value == 0f) DesktopGlyphKind.MUTE else DesktopGlyphKind.VOLUME, if (value == 0f) "Unmute" else "Mute", size = if (compact) 24 else 40, icon = if (compact) 12 else 21, enabled = !preferences.bitPerfectEnabled) {
            val target = if (value == 0f) 0.75f else 0f
            value = target; controller.setVolume(target); settings.setVolume(target)
        }
        NucleusSlider(
            value = value, onValueChange = { value = it; controller.setVolume(it) },
            colors = SliderDefaults.colors(activeTrackColor = palette.foreground.copy(alpha = .7f),
                inactiveTrackColor = palette.foreground.copy(alpha = .25f), thumbColor = palette.foreground),
            enabled = !preferences.bitPerfectEnabled, onValueChangeFinished = { settings.setVolume(value) },
            modifier = Modifier.weight(1f).height(28.dp).semantics { contentDescription = "Volume" },
        )
        if (!compact) Text("${(value * 100).toInt()}%", color = palette.muted, fontSize = 11.sp, modifier = Modifier.width(32.dp))
    }
}

@Composable
internal fun PlayerGlyphButton(kind: DesktopGlyphKind, label: String, selected: Boolean = false, size: Int = 40, icon: Int = 21, enabled: Boolean = true, onClick: () -> Unit) {
    val palette = desktopPalette()
    val settings: PlaybackSettingsStateHolder = koinInject()
    val assets: DesktopPlayerAssets = koinInject()
    val override by remember(settings, kind) { settings.state.map { it.playerCustomization.icons[kind.name] }.distinctUntilChanged() }.collectAsState(null)
    val file = remember(override) { override?.let { assets.file(it.fileName) } }
    var failed by remember(file) { mutableStateOf(false) }
    Box(Modifier.size(size.dp).clip(CircleShape)
        .background(if (selected) palette.foreground.copy(alpha = .1f) else Color.Transparent)
        .semantics { contentDescription = label }
        .desktopInteractive(enabled = enabled, scale = .9f, hoverColor = palette.foreground.copy(alpha = .08f), onClick = onClick),
        contentAlignment = Alignment.Center) {
        val tint = (if (selected) palette.accent else palette.foreground).copy(alpha = if (enabled) 1f else .35f)
        if (file != null && !failed) AsyncImage(file, null, Modifier.size(icon.dp), contentScale = ContentScale.Fit,
            colorFilter = if (override?.tint == true) ColorFilter.tint(tint) else null, onError = { failed = true })
        else DesktopGlyph(kind, tint, icon.dp)
    }
}

private fun playerTime(value: Long): String = "${value / 60_000}:${((value / 1_000) % 60).toString().padStart(2, '0')}"
