/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import com.nexamusic.app.ui.component.GlassSwitchCompat as Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.nexamusic.app.LocalPlayerAwareWindowInsets
import com.nexamusic.app.LocalPlayerConnection
import com.nexamusic.app.R
import com.nexamusic.app.constants.PlayerArtworkStyle
import com.nexamusic.app.constants.PlayerArtworkStyleKey
import com.nexamusic.app.constants.PlayerBackgroundStyle
import com.nexamusic.app.constants.PlayerBackgroundStyleKey
import com.nexamusic.app.constants.UseAppleMusicPlayerKey
import com.nexamusic.app.constants.ShowPlayerThumbnailShadowKey
import com.nexamusic.app.constants.ShowUpNextKey
import com.nexamusic.app.constants.PlayerThumbnailShadowElevationKey
import com.nexamusic.app.constants.PlayerGradientAngleKey
import com.nexamusic.app.constants.PlayerGradientStopsKey
import com.nexamusic.app.constants.PlayerLayoutHiddenSlotsKey
import com.nexamusic.app.constants.PlayerLayoutOrderKey
import com.nexamusic.app.constants.PlayerStaticColorKey
import com.nexamusic.app.constants.SliderStyle
import com.nexamusic.app.constants.SliderStyleKey
import com.nexamusic.app.models.MediaMetadata
import com.nexamusic.app.ui.component.ColorPickerDialog
import com.nexamusic.app.ui.component.SwitchPreference
import com.nexamusic.app.ui.component.SliderPreference
import com.nexamusic.app.ui.component.IconButton as AppIconButton
import com.nexamusic.app.ui.component.shapes.ContinuousRoundedRectangle
import com.nexamusic.app.ui.player.PlayerLayoutRegistry
import com.nexamusic.app.ui.player.PlayerSlot
import com.nexamusic.app.ui.theme.DefaultGradientStops
import com.nexamusic.app.ui.theme.decodeGradientStops
import com.nexamusic.app.ui.theme.encodeGradientStops
import com.nexamusic.app.ui.theme.tiltedGradient
import com.nexamusic.app.ui.utils.appTopBarWindowInsets
import com.nexamusic.app.ui.utils.backToMain
import com.nexamusic.app.utils.rememberEnumPreference
import com.nexamusic.app.utils.rememberPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.roundToInt
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private val PresetCardWidth = 148.dp

/**
 * Player theme picker: preset cards that render a live miniature of the real
 * player, so the artwork shape, background and seek bar are chosen by looking
 * at them rather than by reading a list of names.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerThemeScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (artworkStyle, onArtworkStyleChange) = rememberEnumPreference(
        PlayerArtworkStyleKey, defaultValue = PlayerArtworkStyle.CARD
    )
    val (background, onBackgroundChange) = rememberEnumPreference(
        PlayerBackgroundStyleKey, defaultValue = PlayerBackgroundStyle.APPLE_MUSIC
    )
    val (sliderStyle, onSliderStyleChange) = rememberEnumPreference(
        SliderStyleKey, defaultValue = SliderStyle.DEFAULT
    )
    val (staticColorInt, onStaticColorChange) = rememberPreference(
        PlayerStaticColorKey, defaultValue = 0xFF1A1A1A.toInt()
    )
    val (gradientStopsRaw, onGradientStopsChange) = rememberPreference(
        PlayerGradientStopsKey, defaultValue = ""
    )
    val (gradientAngle, onGradientAngleChange) = rememberPreference(
        PlayerGradientAngleKey, defaultValue = 90f
    )
    val (useAppleMusicPlayer, onUseAppleMusicPlayerChange) = rememberPreference(
        UseAppleMusicPlayerKey, defaultValue = false
    )
    val (showPlayerThumbnailShadow, onShowPlayerThumbnailShadowChange) = rememberPreference(
        ShowPlayerThumbnailShadowKey, defaultValue = false
    )
    val (playerThumbnailShadowElevation, onPlayerThumbnailShadowElevationChange) = rememberPreference(
        PlayerThumbnailShadowElevationKey, defaultValue = 8f
    )
    val (showUpNext, onShowUpNextChange) = rememberPreference(ShowUpNextKey, defaultValue = false)
    val gradientStops = remember(gradientStopsRaw) { decodeGradientStops(gradientStopsRaw) }

    var showStaticPicker by rememberSaveable { mutableStateOf(false) }
    var showGradientSheet by rememberSaveable { mutableStateOf(false) }

    // Layout builder disabled for now — see the commented-out section below.
    // val (layoutOrderRaw, onLayoutOrderChange) = rememberPreference(PlayerLayoutOrderKey, defaultValue = "")
    // val (hiddenSlotsRaw, onHiddenSlotsChange) = rememberPreference(PlayerLayoutHiddenSlotsKey, defaultValue = "")
    // val layoutSlots = remember(layoutOrderRaw) {
    //     PlayerLayoutRegistry.deserializeOrder(layoutOrderRaw).toMutableStateList()
    // }
    // val hiddenSlots = remember(hiddenSlotsRaw) {
    //     PlayerLayoutRegistry.deserializeHiddenSlots(hiddenSlotsRaw)
    // }

    // Apple Music draws its own square artwork treatment, so the shape presets
    // have nothing to act on while it is selected.
    val artworkLocked = background == PlayerBackgroundStyle.APPLE_MUSIC
    // V17 uses its own thumbnail layout — VINYL and CLOVER don't apply.
    val v17Active = useAppleMusicPlayer

    // Preview the song that is actually playing; fall back to the app icon.
    val playerConnection = LocalPlayerConnection.current
    val mediaMetadata by remember(playerConnection) {
        playerConnection?.mediaMetadata ?: MutableStateFlow<MediaMetadata?>(null)
    }.collectAsState()
    val artworkUrl = mediaMetadata?.thumbnailUrl

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState()),
    ) {
        SectionTitle(stringResource(R.string.player_theme_artwork))
        if (artworkLocked || v17Active) {
            LockedNote(stringResource(if (v17Active) R.string.player_theme_artwork_locked_v17 else R.string.player_theme_artwork_locked))
        }
        PresetRow {
            PlayerArtworkStyle.entries.forEach { style ->
                val disabledByV17 = v17Active && style != PlayerArtworkStyle.CARD
                PresetCard(
                    label = when (style) {
                        PlayerArtworkStyle.CARD -> stringResource(R.string.player_theme_card)
                        PlayerArtworkStyle.VINYL -> stringResource(R.string.player_theme_vinyl)
                        PlayerArtworkStyle.CLOVER -> stringResource(R.string.player_theme_clover)
                    },
                    selected = artworkStyle == style,
                    enabled = !artworkLocked && !disabledByV17,
                    onClick = { onArtworkStyleChange(style) },
                ) {
                    PlayerPreview(
                        artworkStyle = style,
                        background = background,
                        sliderStyle = sliderStyle,
                        artworkUrl = artworkUrl,
                        staticColor = Color(staticColorInt),
                        gradientStops = gradientStops,
                        gradientAngle = gradientAngle,
                    )
                }
            }
        }

        SectionTitle(stringResource(R.string.player_background_style))
        PresetRow {
            PlayerBackgroundStyle.entries.filter {
                it != PlayerBackgroundStyle.BLUR || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            }.forEach { style ->
                val disabledByV17 = v17Active && style == PlayerBackgroundStyle.APPLE_MUSIC
                PresetCard(
                    label = backgroundLabel(style),
                    selected = background == style,
                    enabled = !disabledByV17,
                    onClick = { onBackgroundChange(style) },
                ) {
                    PlayerPreview(
                        artworkStyle = artworkStyle,
                        background = style,
                        sliderStyle = sliderStyle,
                        artworkUrl = artworkUrl,
                        staticColor = Color(staticColorInt),
                        gradientStops = gradientStops,
                        gradientAngle = gradientAngle,
                    )
                }
            }
        }

        SectionTitle(stringResource(R.string.apple_music_player_v17))
        PresetRow {
            PresetCard(
                label = stringResource(R.string.apple_music_player_v17),
                selected = useAppleMusicPlayer,
                onClick = { onUseAppleMusicPlayerChange(!useAppleMusicPlayer) },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.convx_logo),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
        }
        if (useAppleMusicPlayer) {
            LockedNote(stringResource(R.string.apple_music_player_v17_description))
            SwitchPreference(
                title = { Text(stringResource(R.string.player_thumbnail_shadow)) },
                checked = showPlayerThumbnailShadow,
                onCheckedChange = onShowPlayerThumbnailShadowChange,
            )
            if (showPlayerThumbnailShadow) {
                SliderPreference(
                    title = { Text(stringResource(R.string.player_thumbnail_shadow_elevation)) },
                    value = playerThumbnailShadowElevation,
                    onValueChange = onPlayerThumbnailShadowElevationChange,
                )
            }
        }
        SwitchPreference(
            title = { Text(stringResource(R.string.show_up_next)) },
            description = stringResource(R.string.show_up_next_desc),
            checked = showUpNext,
            onCheckedChange = onShowUpNextChange,
        )

        if (background == PlayerBackgroundStyle.STATIC) {
            SettingRow(
                title = stringResource(R.string.player_theme_static_color),
                onClick = { showStaticPicker = true },
            ) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(staticColorInt))
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
        }

        if (background == PlayerBackgroundStyle.CUSTOM_GRADIENT) {
            SettingRow(
                title = stringResource(R.string.player_theme_edit_gradient),
                onClick = { showGradientSheet = true },
            ) {
                Box(
                    Modifier
                        .width(56.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .tiltedGradient(gradientStops, gradientAngle)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                )
            }
        }

        SectionTitle(stringResource(R.string.slider_style))
        PresetRow {
            SliderStyle.entries.forEach { style ->
                PresetCard(
                    label = sliderLabel(style),
                    selected = sliderStyle == style,
                    onClick = { onSliderStyleChange(style) },
                ) {
                    PlayerPreview(
                        artworkStyle = artworkStyle,
                        background = background,
                        sliderStyle = style,
                        artworkUrl = artworkUrl,
                        staticColor = Color(staticColorInt),
                        gradientStops = gradientStops,
                        gradientAngle = gradientAngle,
                    )
                }
            }
        }

        // Layout builder disabled for now.
        // SectionTitle(stringResource(R.string.player_theme_layout))
        // Text(
        //     text = stringResource(R.string.player_theme_layout_desc),
        //     style = MaterialTheme.typography.bodySmall,
        //     color = MaterialTheme.colorScheme.onSurfaceVariant,
        //     modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        // )
        // PlayerLayoutList(
        //     slots = layoutSlots,
        //     hiddenSlots = hiddenSlots,
        //     artworkUrl = artworkUrl,
        //     onReordered = { onLayoutOrderChange(PlayerLayoutRegistry.serializeOrder(it)) },
        //     onVisibilityChange = { slot, hidden ->
        //         val updated = if (hidden) hiddenSlots + slot else hiddenSlots - slot
        //         onHiddenSlotsChange(PlayerLayoutRegistry.serializeHiddenSlots(updated))
        //     },
        //     modifier = Modifier
        //         .fillMaxWidth()
        //         .padding(horizontal = 16.dp)
        //         .height(480.dp),
        // )

        Spacer(Modifier.height(24.dp))
    }

    if (showStaticPicker) {
        ColorPickerDialog(
            initialColor = Color(staticColorInt),
            title = stringResource(R.string.player_theme_static_color),
            onDismiss = { showStaticPicker = false },
            onConfirm = { onStaticColorChange(it.toArgb()); showStaticPicker = false },
            defaultColor = Color(0xFF1A1A1A),
        )
    }

    if (showGradientSheet) {
        GradientSheet(
            stops = gradientStops,
            angle = gradientAngle,
            onDismiss = { showGradientSheet = false },
            onStopsChange = { onGradientStopsChange(encodeGradientStops(it)) },
            onAngleChange = onGradientAngleChange,
        )
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.player_theme)) },
        navigationIcon = {
            AppIconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

/**
 * Multi-stop gradient editor: tap a stop to recolour it, add or remove stops, and
 * tilt the whole thing. Every control writes straight through, so the preset card
 * behind the sheet updates as you drag.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradientSheet(
    stops: List<Color>,
    angle: Float,
    onDismiss: () -> Unit,
    onStopsChange: (List<Color>) -> Unit,
    onAngleChange: (Float) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val working = remember(stops) { stops.toMutableStateList() }
    var editingIndex by remember { mutableIntStateOf(-1) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
        ) {
            Text(
                text = stringResource(R.string.player_theme_custom_gradient),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .tiltedGradient(working.toList(), angle)
            )

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.player_theme_stops), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                working.forEachIndexed { index, color ->
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clickable { editingIndex = index }
                    )
                }
                // Cap at five: past that the stops are too close to tell apart.
                if (working.size < 5) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clickable {
                                working.add(working.lastOrNull() ?: Color.Black)
                                onStopsChange(working.toList())
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painterResource(R.drawable.add),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            if (working.size > 2) {
                TextButton(
                    onClick = {
                        working.removeAt(working.lastIndex)
                        onStopsChange(working.toList())
                    }
                ) { Text(stringResource(R.string.player_theme_remove_stop)) }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.player_theme_tilt, angle.roundToInt()),
                style = MaterialTheme.typography.titleSmall,
            )
            Slider(
                value = angle,
                onValueChange = onAngleChange,
                valueRange = 0f..360f,
            )

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GradientPresetChip(stringResource(R.string.player_theme_tilt_vertical)) { onAngleChange(90f) }
                GradientPresetChip(stringResource(R.string.player_theme_tilt_horizontal)) { onAngleChange(0f) }
                GradientPresetChip(stringResource(R.string.player_theme_tilt_diagonal)) { onAngleChange(45f) }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = {
                    working.clear()
                    working.addAll(DefaultGradientStops)
                    onStopsChange(DefaultGradientStops)
                    onAngleChange(90f)
                }
            ) { Text(stringResource(R.string.reset)) }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (editingIndex in working.indices) {
        val index = editingIndex
        ColorPickerDialog(
            initialColor = working[index],
            title = stringResource(R.string.player_theme_stops),
            onDismiss = { editingIndex = -1 },
            onConfirm = { color ->
                working[index] = color
                onStopsChange(working.toList())
                editingIndex = -1
            },
        )
    }
}

@Composable
private fun GradientPresetChip(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
private fun backgroundLabel(style: PlayerBackgroundStyle) = when (style) {
    PlayerBackgroundStyle.DEFAULT -> stringResource(R.string.follow_theme)
    PlayerBackgroundStyle.GRADIENT -> stringResource(R.string.gradient)
    PlayerBackgroundStyle.BLUR -> stringResource(R.string.player_background_blur)
    PlayerBackgroundStyle.GLOW_ANIMATED -> stringResource(R.string.glow_animated)
    PlayerBackgroundStyle.APPLE_MUSIC -> stringResource(R.string.apple_music)
    PlayerBackgroundStyle.LIVE_MESH -> stringResource(R.string.live_mesh)
    PlayerBackgroundStyle.STATIC -> stringResource(R.string.player_theme_static)
    PlayerBackgroundStyle.CUSTOM_GRADIENT -> stringResource(R.string.player_theme_custom_gradient)
}

@Composable
private fun sliderLabel(style: SliderStyle) = when (style) {
    SliderStyle.DEFAULT -> stringResource(R.string.default_style)
    SliderStyle.WAVY -> stringResource(R.string.wavy)
    SliderStyle.SLIM -> stringResource(R.string.slim)
    SliderStyle.WAVEFORM -> stringResource(R.string.waveform)
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun LockedNote(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
    )
}

/**
 * Drag-to-reorder list of [PlayerSlot]s, rendered as a miniature of the real
 * Now Playing screen — each slot is its actual visual shape (artwork square,
 * title/artist bars, seek bar, transport row, action row) rather than a
 * settings-style text row, so the builder shows what the layout will look
 * like instead of naming it. A switch on each hideable block toggles it off;
 * [PlayerSlot.CONTROLS] is reorder-only, no toggle, since playback needs it.
 */
@Composable
private fun PlayerLayoutList(
    slots: MutableList<PlayerSlot>,
    hiddenSlots: Set<PlayerSlot>,
    artworkUrl: String?,
    onReordered: (List<PlayerSlot>) -> Unit,
    onVisibilityChange: (PlayerSlot, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    var hasDragged by remember { mutableStateOf(false) }
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
    ) { from, to ->
        val movedItem = slots.removeAt(from.index)
        slots.add(to.index, movedItem)
        hasDragged = true
    }

    LaunchedEffect(reorderableState.isAnyItemDragging) {
        if (!reorderableState.isAnyItemDragging && hasDragged) {
            onReordered(slots.toList())
            hasDragged = false
        }
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier,
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(slots, key = { _, slot -> slot.name }) { _, slot ->
                ReorderableItem(state = reorderableState, key = slot.name) {
                    val isHidden = slot in hiddenSlots
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isHidden) 0.4f else 1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .draggableHandle()
                            .padding(10.dp),
                    ) {
                        SlotPreviewBlock(slot = slot, artworkUrl = artworkUrl)

                        if (slot.hideable) {
                            Switch(
                                checked = !isHidden,
                                onCheckedChange = { visible -> onVisibilityChange(slot, !visible) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .scale(0.7f),
                                thumbContent = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (!isHidden) R.drawable.check else R.drawable.close
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** One slot's actual visual shape, at reduced scale, standing in for its real Now Playing section. */
@Composable
private fun SlotPreviewBlock(slot: PlayerSlot, artworkUrl: String?) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    when (slot) {
        PlayerSlot.ALBUM_ART -> Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(ContinuousRoundedRectangle(10.dp))
            ) {
                AsyncImage(
                    model = artworkUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    fallback = painterResource(R.drawable.convx_logo),
                    error = painterResource(R.drawable.convx_logo),
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(slot.labelRes),
                style = MaterialTheme.typography.labelMedium,
                color = onSurface.copy(alpha = 0.6f),
            )
        }

        PlayerSlot.TRACK_INFO -> Column(modifier = Modifier.padding(end = 40.dp)) {
            Bar(onSurface.copy(alpha = 0.85f), widthFraction = 0.55f, height = 9.dp)
            Spacer(Modifier.height(6.dp))
            Bar(onSurface.copy(alpha = 0.45f), widthFraction = 0.35f, height = 7.dp)
        }

        PlayerSlot.SEEK_BAR -> Box(modifier = Modifier.padding(end = 40.dp)) {
            SeekBarPreview(SliderStyle.DEFAULT, onSurface.copy(alpha = 0.85f))
        }

        PlayerSlot.CONTROLS -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dot(onSurface.copy(alpha = 0.5f), 14.dp)
            Spacer(Modifier.width(16.dp))
            Dot(onSurface, 26.dp)
            Spacer(Modifier.width(16.dp))
            Dot(onSurface.copy(alpha = 0.5f), 14.dp)
        }

        PlayerSlot.ACTION_ROW -> Row(
            modifier = Modifier.padding(end = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            repeat(3) { Dot(onSurface.copy(alpha = 0.35f), 16.dp) }
        }
    }
}

@Composable
private fun PresetRow(content: @Composable () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) { content() }
}

@Composable
private fun PresetCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    preview: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(PresetCardWidth)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = enabled, onClick = onClick)
            // Disabled presets stay readable but visibly out of play.
            .alpha(if (enabled) 1f else 0.38f)
            .padding(bottom = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.62f)
                .shadow(if (selected) 10.dp else 5.dp, shape)
                .clip(shape)
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    shape = shape,
                )
        ) { preview() }

        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                )
                .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Icon(
                    painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(13.dp),
                )
            }
        }
    }
}

/** Miniature of the real player: background wash + artwork shape + seek bar. */
@Composable
private fun PlayerPreview(
    artworkStyle: PlayerArtworkStyle,
    background: PlayerBackgroundStyle,
    sliderStyle: SliderStyle,
    artworkUrl: String?,
    staticColor: Color,
    gradientStops: List<Color>,
    gradientAngle: Float,
) {
    val accent = MaterialTheme.colorScheme.primary
    val surface = MaterialTheme.colorScheme.surface
    val backdrop: Brush? = when (background) {
        PlayerBackgroundStyle.DEFAULT -> Brush.verticalGradient(listOf(surface, surface))
        PlayerBackgroundStyle.STATIC -> Brush.verticalGradient(listOf(staticColor, staticColor))
        // Drawn by tiltedGradient below so the tilt shows in the preview too.
        PlayerBackgroundStyle.CUSTOM_GRADIENT -> null
        PlayerBackgroundStyle.LIVE_MESH ->
            Brush.linearGradient(listOf(accent, Color.Black, accent.copy(alpha = 0.4f)))
        PlayerBackgroundStyle.GLOW_ANIMATED -> Brush.radialGradient(listOf(accent, Color.Black))
        else -> Brush.verticalGradient(listOf(accent.copy(alpha = 0.55f), Color.Black))
    }
    val onBackdrop = if (background == PlayerBackgroundStyle.DEFAULT) {
        MaterialTheme.colorScheme.onSurface
    } else {
        Color.White
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (backdrop != null) {
                    Modifier.background(backdrop)
                } else {
                    Modifier.tiltedGradient(gradientStops, gradientAngle)
                }
            )
            .padding(12.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        // Real player: APPLE_MUSIC draws full-bleed unclipped artwork in portrait, ignoring the
        // artwork-style shape entirely — a VINYL/CLOVER style must not preview as a circle here.
        val previewShape = if (background == PlayerBackgroundStyle.APPLE_MUSIC) {
            RoundedCornerShape(0.dp)
        } else when (artworkStyle) {
            PlayerArtworkStyle.CARD -> ContinuousRoundedRectangle(10.dp)
            else -> CircleShape
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(previewShape),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                fallback = painterResource(R.drawable.convx_logo),
                error = painterResource(R.drawable.convx_logo),
                modifier = Modifier.fillMaxSize(),
            )
            if (previewShape == CircleShape && artworkStyle == PlayerArtworkStyle.VINYL) {
                Canvas(Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.82f),
                        radius = r * 0.67f,
                        style = Stroke(width = r * 0.66f),
                    )
                    drawCircle(Color.Black, radius = r * 0.07f)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Bar(onBackdrop.copy(alpha = 0.9f), widthFraction = 0.8f, height = 7.dp)
        Spacer(Modifier.height(5.dp))
        Bar(onBackdrop.copy(alpha = 0.45f), widthFraction = 0.55f, height = 5.dp)
        Spacer(Modifier.height(12.dp))
        SeekBarPreview(sliderStyle, onBackdrop)
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Dot(onBackdrop.copy(alpha = 0.6f), 8.dp)
            Dot(onBackdrop, 20.dp)
            Dot(onBackdrop.copy(alpha = 0.6f), 8.dp)
        }
    }
}

/** Tiny stand-in for each seek bar style, drawn at roughly a third of playback. */
@Composable
private fun SeekBarPreview(style: SliderStyle, color: Color) {
    when (style) {
        SliderStyle.DEFAULT -> Canvas(Modifier.fillMaxWidth().height(10.dp)) {
            val y = size.height / 2f
            val split = size.width * 0.35f
            drawLine(color.copy(alpha = 0.3f), androidx.compose.ui.geometry.Offset(0f, y),
                androidx.compose.ui.geometry.Offset(size.width, y), strokeWidth = 3f)
            drawLine(color, androidx.compose.ui.geometry.Offset(0f, y),
                androidx.compose.ui.geometry.Offset(split, y), strokeWidth = 3f)
            drawCircle(color, radius = size.height * 0.35f,
                center = androidx.compose.ui.geometry.Offset(split, y))
        }

        SliderStyle.WAVY -> Canvas(Modifier.fillMaxWidth().height(10.dp)) {
            val y = size.height / 2f
            val split = size.width * 0.35f
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, y)
                var x = 0f
                var up = true
                while (x < split) {
                    val next = (x + 6f).coerceAtMost(split)
                    quadraticTo(x + 3f, if (up) y - 5f else y + 5f, next, y)
                    x = next
                    up = !up
                }
            }
            drawPath(path, color, style = Stroke(width = 3f))
            drawLine(color.copy(alpha = 0.3f), androidx.compose.ui.geometry.Offset(split, y),
                androidx.compose.ui.geometry.Offset(size.width, y), strokeWidth = 3f)
        }

        SliderStyle.SLIM -> Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .weight(0.35f)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(2.dp))
            Box(
                Modifier
                    .weight(0.65f)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.3f))
            )
        }

        SliderStyle.WAVEFORM -> Canvas(Modifier.fillMaxWidth().height(16.dp)) {
            val bars = 14
            val slot = size.width / bars
            val barW = slot * 0.5f
            val mid = size.height / 2f
            val heights = com.nexamusic.app.ui.component.waveformBars(7, bars)
            for (i in 0 until bars) {
                val h = (heights[i] * size.height).coerceAtLeast(size.height * 0.2f)
                val x = i * slot + slot / 2f
                drawRoundRect(
                    color = if (i < bars / 2) color else color.copy(alpha = 0.3f),
                    topLeft = androidx.compose.ui.geometry.Offset(x - barW / 2f, mid - h / 2f),
                    size = androidx.compose.ui.geometry.Size(barW, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(barW / 2f),
                )
            }
        }
    }
}

@Composable
private fun Bar(color: Color, widthFraction: Float, height: Dp) {
    Box(
        Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun Dot(color: Color, size: Dp) {
    Box(Modifier.size(size).clip(CircleShape).background(color))
}

@Composable
private fun SettingRow(title: String, onClick: () -> Unit, trailing: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        trailing()
    }
}
