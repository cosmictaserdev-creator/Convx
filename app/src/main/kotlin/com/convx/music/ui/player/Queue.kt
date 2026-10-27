/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import com.convx.music.ui.utils.vibrantChrome
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.utils.fadingEdge
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.selection.toggleable
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.source.ShuffleOrder.DefaultShuffleOrder
import androidx.navigation.NavController
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.AutoLoadMoreKey
import com.convx.music.constants.CrossfadeEnabledKey
import com.convx.music.constants.ListItemHeight
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.constants.QueueEditLockKey
import com.convx.music.constants.ShowCommentButtonKey
import com.convx.music.constants.UseNewPlayerDesignKey
import com.convx.music.extensions.metadata
import com.convx.music.extensions.move
import com.convx.music.extensions.toggleRepeatMode
import com.convx.music.listentogether.RoomRole
import com.convx.music.models.MediaMetadata
import com.convx.music.ui.component.ActionPromptDialog
import com.convx.music.ui.component.BottomSheet
import com.convx.music.ui.component.BottomSheetState
import com.convx.music.ui.component.LocalBottomSheetPageState
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.MediaMetadataListItem
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.component.thumbnailPx
import com.convx.music.ui.menu.PlayerMenu
import com.convx.music.ui.menu.QueueMenu
import com.convx.music.ui.menu.SelectionMediaMetadataMenu
import com.convx.music.ui.screens.CommentSheet
import com.convx.music.ui.utils.ShowMediaInfo
import com.convx.music.utils.listItemShape
import com.convx.music.utils.makeTimeString
import com.convx.music.utils.rememberPreference
import androidx.compose.material.icons.Icons
import android.bluetooth.BluetoothDevice
import com.convx.music.core.rememberHeadphoneConnected
import com.convx.music.core.AudioDeviceMenu
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import android.widget.Toast
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.convx.music.ui.utils.additiveChrome
import coil3.size.Size as CoilSize
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Queue(
    state: BottomSheetState,
    playerBottomSheetState: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    background: Color,
    onBackgroundColor: Color,
    TextBackgroundColor: Color,
    textButtonColor: Color,
    iconButtonColor: Color,
    pureBlack: Boolean,
    showInlineLyrics: Boolean,
    playerBackground: PlayerBackgroundStyle = PlayerBackgroundStyle.DEFAULT,
    onToggleLyrics: () -> Unit = {},
    onOpenQueue: (() -> Unit)? = null,
    /** Hide V17's duplicate controls; the lock toggle moves to the Continue Playing row. */
    showHeaderRow: Boolean = true,
    /** Disable sheet dragging when a parent pane owns vertical gestures. */
    sheetDraggable: Boolean = true,
    /** Disable sheet back handling when the host switches panes itself. */
    handleBack: Boolean = true,
    /** Disable when the host already applies the top system inset. */
    applyTopSystemInset: Boolean = true,
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboard.current
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var showAudioDeviceBottomSheet by remember { mutableStateOf(false) }
    var showRoutePopover by remember { mutableStateOf(false) }


    /** Artist subtitle opacity. */
    val FlamingoSecondaryAlpha = 0.35f

    /** Elapsed and remaining time opacity. */
    val FlamingoTimeAlpha = 0.3f

    /** Content tint opacity for additive blending. */
    val AdditiveChromeContentAlpha = 0.62f

    // Match PlayerV2's theme-independent additive colors.
    val chromeAdditive = true
    val chromePrimary = if (background.luminance() > 0.55f) Color.Black else Color.White
    val chromeSecondary = chromePrimary.copy(alpha = FlamingoSecondaryAlpha)
    val chromeArtist = chromePrimary.copy(alpha = 0.78f)
    val chromePillContainer = chromePrimary.copy(alpha = 0.12f)
    // Keep the active pill translucent so additiveChrome preserves its white glyph.
    val chromePillActive = chromePrimary.copy(alpha = 0.28f)

    val isBluetoothConnected by rememberHeadphoneConnected()

    // Listen Together state (reactive)
    val listenTogetherManager = LocalListenTogetherManager.current
    val listenTogetherRoleState = listenTogetherManager?.role?.collectAsState(initial = com.convx.music.listentogether.RoomRole.NONE)
    // See Player.kt: gated on control mode, not on role.
    val canControlTogether = listenTogetherManager?.canControl?.collectAsState(initial = true)
    val isListenTogetherGuest = canControlTogether?.value == false

    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val repeatMode by playerConnection.repeatMode.collectAsStateWithLifecycle()

    val currentWindowIndex by playerConnection.currentWindowIndex.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val currentSong by playerConnection.currentSong.collectAsState(initial = null)
    val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsStateWithLifecycle()

    val currentFormat by playerConnection.currentFormat.collectAsState(initial = null)

    val selectedSongs = remember { mutableStateListOf<MediaMetadata>() }
    val selectedItems = remember { mutableStateListOf<Timeline.Window>() }

    // Casting can be unavailable while the service initializes.
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val castIsPlaying by castHandler?.castIsPlaying?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<String>, String>(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf() }
    val onExitSelectionMode = {
        inSelectMode = false
        selection.clear()
    }
    if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }

    var locked by rememberPreference(QueueEditLockKey, defaultValue = true)

    val (useNewPlayerDesign, onUseNewPlayerDesignChange) = rememberPreference(
        UseNewPlayerDesignKey,
        defaultValue = false
    )
    val (showCommentButton) = rememberPreference(
        ShowCommentButtonKey,
        defaultValue = true
    )

    val snackbarHostState = remember { SnackbarHostState() }
    var dismissJob: Job? by remember { mutableStateOf(null) }

    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var sleepTimerValue by remember { mutableFloatStateOf(30f) }
    val sleepTimerEnabled = remember(
        playerConnection.service.sleepTimer.triggerTime,
        playerConnection.service.sleepTimer.pauseWhenSongEnd
    ) {
        playerConnection.service.sleepTimer.isActive
    }
    var sleepTimerTimeLeft by remember { mutableLongStateOf(0L) }
    var showCommentSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(sleepTimerEnabled) {
        if (sleepTimerEnabled) {
            while (isActive) {
                sleepTimerTimeLeft = if (playerConnection.service.sleepTimer.pauseWhenSongEnd) {
                    playerConnection.player.duration - playerConnection.player.currentPosition
                } else {
                    playerConnection.service.sleepTimer.triggerTime - System.currentTimeMillis()
                }
                delay(1000L)
            }
        }
    }

    BottomSheet(
        state = state,
        modifier = modifier,
        isExpandable = sheetDraggable,
        handleBack = handleBack,
        background = {
            Box(Modifier.fillMaxSize().background(Color.Unspecified))
        },
        collapsedContent = {
            if (useNewPlayerDesign) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 12.dp)
                        .windowInsetsPadding(
                            WindowInsets.systemBars.only(
                                WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                            ),
                        ),
                ) {
                    val buttonSize = 42.dp
                    val iconSize = 24.dp
                    val queueShape = RoundedCornerShape(
                        topStart = 50.dp, bottomStart = 50.dp,
                        topEnd = 3.dp, bottomEnd = 3.dp
                    )
                    val middleShape = RoundedCornerShape(3.dp)
                    val repeatShape = RoundedCornerShape(
                        topStart = 3.dp, bottomStart = 3.dp,
                        topEnd = 50.dp, bottomEnd = 50.dp
                    )

                    PlayerQueueButton(
                        icon =  R.drawable.flamingo_queue,
                        iconSlot = com.convx.music.ui.player.customize.PlayerIconSlot.V2_QUEUE,
                        onClick = { onOpenQueue?.invoke() ?: state.expandSoft() },
                        isActive = false,
                        shape = queueShape,
                        modifier = Modifier.size(buttonSize),
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        iconSize = 28.dp,
                        textBackgroundColor = TextBackgroundColor,
                        playerBackground = playerBackground
                    )

                    PlayerQueueButton(
                        icon = R.drawable.bedtime,
                        onClick = {
                            if (sleepTimerEnabled) {
                                playerConnection.service.sleepTimer.clear()
                            } else {
                                showSleepTimerDialog = true
                            }
                        },
                        isActive = sleepTimerEnabled,
                        enabled = !isListenTogetherGuest,
                        shape = middleShape,
                        modifier = Modifier.size(buttonSize),
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        text = if (sleepTimerEnabled) makeTimeString(sleepTimerTimeLeft) else null,
                        iconSize = iconSize,
                        textBackgroundColor = TextBackgroundColor,
                        playerBackground = playerBackground
                    )

                    PlayerQueueButton(
                        icon = R.drawable.flamingo_lyrics,
                        iconSlot = com.convx.music.ui.player.customize.PlayerIconSlot.V2_LYRICS,
                        onClick = { onToggleLyrics() },
                        isActive = showInlineLyrics,
                        shape = middleShape,
                        modifier = Modifier.size(buttonSize),
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        iconSize = 28.dp,
                        textBackgroundColor = TextBackgroundColor,
                        playerBackground = playerBackground
                    )

                    if (showCommentButton) {
                        PlayerQueueButton(
                            icon = R.drawable.chat_msg,
                            onClick = { showCommentSheet = true },
                            isActive = showCommentSheet,
                            shape = middleShape,
                            modifier = Modifier.size(buttonSize),
                            textButtonColor = textButtonColor,
                            iconButtonColor = iconButtonColor,
                            iconSize = iconSize,
                            textBackgroundColor = TextBackgroundColor,
                            playerBackground = playerBackground
                        )
                    }

                    val shuffleModeEnabledInside by playerConnection.shuffleModeEnabled.collectAsStateWithLifecycle()
                    PlayerQueueButton(
                        icon = R.drawable.shuffle,
                        onClick = {
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(
                                    if (shuffleModeEnabledInside) currentWindowIndex else 0,
                                )
                            }.invokeOnCompletion {
                                playerConnection.player.shuffleModeEnabled = !shuffleModeEnabledInside
                            }
                        },
                        isActive = shuffleModeEnabledInside,
                        enabled = !isListenTogetherGuest,
                        shape = middleShape,
                        modifier = Modifier.size(buttonSize),
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        iconSize = iconSize,
                        textBackgroundColor = TextBackgroundColor,
                        playerBackground = playerBackground
                    )


                    PlayerQueueButton(
                        icon = when (repeatMode) {
                            Player.REPEAT_MODE_ALL -> R.drawable.repeat
                            Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                            else -> R.drawable.repeat
                        },
                        onClick = {
                            playerConnection.player.toggleRepeatMode()
                        },
                        isActive = repeatMode != Player.REPEAT_MODE_OFF,
                        enabled = !isListenTogetherGuest,
                        shape = middleShape,
                        modifier = Modifier.size(buttonSize),
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        iconSize = iconSize,
                        textBackgroundColor = TextBackgroundColor,
                        playerBackground = playerBackground
                    )

                    // Share the queue-end recommendation setting with Settings > Player.
                    var keepPlaying by rememberPreference(AutoLoadMoreKey, defaultValue = true)
                    PlayerQueueButton(
                        icon = R.drawable.ic_queue_infinity,
                        onClick = { keepPlaying = !keepPlaying },
                        isActive = keepPlaying,
                        enabled = !isListenTogetherGuest,
                        shape = middleShape,
                        modifier = Modifier.size(buttonSize),
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        iconSize = iconSize,
                        textBackgroundColor = TextBackgroundColor,
                        playerBackground = playerBackground
                    )

                    // MusicService reads this preference to control crossfades.
                    var crossfadeOn by rememberPreference(CrossfadeEnabledKey, defaultValue = false)
                    PlayerQueueButton(
                        icon = R.drawable.ic_queue_crossfade,
                        onClick = { crossfadeOn = !crossfadeOn },
                        isActive = crossfadeOn,
                        enabled = !isListenTogetherGuest,
                        shape = repeatShape,
                        modifier = Modifier.size(buttonSize),
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        iconSize = iconSize,
                        textBackgroundColor = TextBackgroundColor,
                        playerBackground = playerBackground
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(buttonSize)
                            .clip(CircleShape)
                            .background(chromePillActive)
                            .clickable {
                                menuState.show {
                                    PlayerMenu(
                                        mediaMetadata = mediaMetadata,
                                        navController = navController,
                                        playerBottomSheetState = playerBottomSheetState,
                                        onShowDetailsDialog = {
                                            mediaMetadata?.id?.let {
                                                bottomSheetPageState.showFloating {
                                                    ShowMediaInfo(it)
                                                }
                                            }
                                        },
                                        onDismiss = menuState::dismiss
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, modifier = Modifier
                                .size(iconSize)
                                .additiveChrome(true)
                                .graphicsLayer { alpha = 0.45f }, tint = Color.White)
                    }
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 12.dp)
                        .windowInsetsPadding(
                            WindowInsets.systemBars
                                .only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
                        ),
                ) {
                    // Balance unequal side buttons so the speaker and sleep controls stay centered.
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    TextButton(
                        onClick = { onOpenQueue?.invoke() ?: state.expandSoft() },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            com.convx.music.ui.player.customize.PlayerGlyph(
                                slot = com.convx.music.ui.player.customize.PlayerIconSlot.V2_QUEUE,
                                fallbackSymbol = sf("list.bullet"),
                                size = 28.dp,
                                bounce = true,
                                contentDescription = stringResource(R.string.queue),
                                modifier = Modifier.additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha },
                                tint = chromePrimary,
                            )
                        }
                    }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                        modifier = Modifier.width(120.dp)
                    ) {
                        ToggleButton(
                            checked = false,
                            onCheckedChange = {
                                showRoutePopover = true
                            },
                            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes(),
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f),
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = chromePillContainer,
                                contentColor = chromeSecondary,
                                checkedContainerColor = chromePillActive,
                                checkedContentColor = chromePrimary
                            )
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isBluetoothConnected) R.drawable.headset_applemusic else R.drawable.speaker_apple
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }
                            )
                        }

                        ToggleButton(
                            checked = sleepTimerEnabled,
                            onCheckedChange = {
                                if (!isListenTogetherGuest) {
                                    if (sleepTimerEnabled) {
                                        playerConnection.service.sleepTimer.clear()
                                    } else {
                                        showSleepTimerDialog = true
                                    }
                                }
                            },
                            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes(),
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f),
                            colors = ToggleButtonDefaults.toggleButtonColors(
                                containerColor = chromePillContainer,
                                contentColor = chromeSecondary,
                                checkedContainerColor = chromePillActive,
                                checkedContentColor = chromePrimary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha },
                            ) {
                                SymbolIcon(symbol = sf("timer"), contentDescription = null, size = 30.dp)
                                if (sleepTimerEnabled) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = makeTimeString(sleepTimerTimeLeft),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    TextButton(
                        onClick = {
                            onToggleLyrics()
                        },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            com.convx.music.ui.player.customize.PlayerGlyph(
                                slot = com.convx.music.ui.player.customize.PlayerIconSlot.V2_LYRICS,
                                fallbackSymbol = sf("quote.bubble"),
                                size = 28.dp,
                                bounce = true,
                                contentDescription = stringResource(R.string.lyrics),
                                modifier = Modifier.additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha },
                                tint = chromePrimary,
                            )
                        }
                    }
                    }
                }
            }
            if (showRoutePopover) {
                V2RoutePopover(
                    onDismiss = { showRoutePopover = false },
                    title = mediaMetadata?.title.orEmpty(),
                    artist = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                    artworkUrl = mediaMetadata?.thumbnailUrl,
                    onMore = { showAudioDeviceBottomSheet = true },
                )
            }
            if (showAudioDeviceBottomSheet) {
                AudioDeviceMenu(onDismiss = { showAudioDeviceBottomSheet = false })
            }

            if (showSleepTimerDialog) {
                ActionPromptDialog(
                    titleBar = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.sleep_timer),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    },
                    onDismiss = { showSleepTimerDialog = false },
                    onConfirm = {
                        showSleepTimerDialog = false
                        playerConnection.service.sleepTimer.start(sleepTimerValue.roundToInt())
                    },
                    onCancel = {
                        showSleepTimerDialog = false
                    },
                    onReset = {
                        sleepTimerValue = 30f // Default value
                    },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = pluralStringResource(
                                    R.plurals.minute,
                                    sleepTimerValue.roundToInt(),
                                    sleepTimerValue.roundToInt()
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Spacer(Modifier.height(16.dp))

                            Slider(
                                value = sleepTimerValue,
                                onValueChange = { sleepTimerValue = it },
                                valueRange = 5f..120f,
                                steps = (120 - 5) / 5 - 1,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    showSleepTimerDialog = false
                                    playerConnection.service.sleepTimer.start(-1)
                                }
                            ) {
                                Text(stringResource(R.string.end_of_song))
                            }
                        }
                    }
                )
            }
        },
    ) {
        val queueTitle by playerConnection.queueTitle.collectAsStateWithLifecycle()
        val queueWindows by playerConnection.queueWindows.collectAsStateWithLifecycle()
        val automix by playerConnection.service.automixItems.collectAsStateWithLifecycle()
        val mutableQueueWindows = remember { mutableStateListOf<Timeline.Window>() }
        val queueLength =
            remember(queueWindows) {
                queueWindows.sumOf { it.mediaItem.metadata!!.duration }
            }

        val headerItems = 1
        var dragInfo by remember { mutableStateOf<Pair<Int, Int>?>(null) }

        val currentPlayingUid = remember(currentWindowIndex, queueWindows) {
            if (currentWindowIndex in queueWindows.indices) {
                queueWindows[currentWindowIndex].uid
            } else null
        }

        val reorderableState = rememberReorderableLazyListState(
            lazyListState = lazyListState,
            scrollThresholdPadding = WindowInsets.systemBars.add(
                WindowInsets(
                    top = ListItemHeight,
                    bottom = ListItemHeight
                )
            ).asPaddingValues()
        ) { from, to ->
            val currentDragInfo = dragInfo
            dragInfo = if (currentDragInfo == null) {
                from.index to to.index
            } else {
                currentDragInfo.first to to.index
            }

            val safeFrom = (from.index - headerItems).coerceIn(0, mutableQueueWindows.lastIndex)
            val safeTo = (to.index - headerItems).coerceIn(0, mutableQueueWindows.lastIndex)

            mutableQueueWindows.move(safeFrom, safeTo)
        }

        LaunchedEffect(reorderableState.isAnyItemDragging) {
            if (!reorderableState.isAnyItemDragging) {
                // Clear first so a failed commit cannot leave stale state for the next drag.
                val info = dragInfo
                dragInfo = null
                if (info != null && queueWindows.isNotEmpty()) {
                    val (from, to) = info
                    val safeFrom = (from - headerItems).coerceIn(0, queueWindows.lastIndex)
                    val safeTo = (to - headerItems).coerceIn(0, queueWindows.lastIndex)

                    if (!playerConnection.player.shuffleModeEnabled) {
                        playerConnection.player.moveMediaItem(safeFrom, safeTo)
                    } else {
                        playerConnection.player.setShuffleOrder(
                            DefaultShuffleOrder(
                                queueWindows.map { it.firstPeriodIndex }
                                    .toMutableList()
                                    .move(safeFrom, safeTo)
                                    .toIntArray(),
                                System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
        }

        LaunchedEffect(queueWindows) {
            mutableQueueWindows.apply {
                clear()
                addAll(queueWindows)
            }
        }

        LaunchedEffect(currentWindowIndex) {
            if (currentWindowIndex != -1 && !reorderableState.isAnyItemDragging) {
                lazyListState.scrollToItem(currentWindowIndex)
            }
        }

        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .background(background),
        ) {
            Column(
                modifier =
                Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { }
                    .windowInsetsPadding(
                        WindowInsets.systemBars
                            .only(
                                if (applyTopSystemInset) WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                                else WindowInsetsSides.Horizontal
                            ),
                    ),
            ) {
                if (showHeaderRow) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(mediaMetadata?.thumbnailUrl)
                            .size(CoilSize(thumbnailPx(48.dp), thumbnailPx(48.dp)))
                            .crossfade(false)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mediaMetadata?.title.orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = chromePrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = chromeArtist,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    val likeDescription = if (currentSong?.song?.liked == true) stringResource(R.string.action_remove_like) else stringResource(R.string.action_like)
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = { PlainTooltip { Text(likeDescription) } },
                        state = rememberTooltipState(),
                    ) {
                        FilledTonalIconButton(
                            onClick = playerConnection::toggleLike,
                            modifier = Modifier.padding(end = 8.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = chromePillContainer,
                                contentColor = chromePrimary,
                            )
                        ) {
                            SymbolIcon(
                                symbol = if (currentSong?.song?.liked == true) sf("heart.fill") else sf("heart"),
                                contentDescription = likeDescription,
                                modifier = Modifier
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = if (currentSong?.song?.liked == true) MaterialTheme.colorScheme.error else chromePrimary)
                        }
                    }

                    val lockDescription = if (locked) stringResource(R.string.unlock_queue) else stringResource(R.string.lock_queue)
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = { PlainTooltip { Text(lockDescription) } },
                        state = rememberTooltipState(),
                    ) {
                        FilledTonalIconButton(
                            onClick = { locked = !locked },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = chromePillContainer,
                                contentColor = chromePrimary,
                            )
                        ) {
                            SymbolIcon(symbol = if (locked) sf("lock.fill") else sf("lock.open.fill"), contentDescription = lockDescription, modifier = Modifier
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                        }
                    }

                    val moreDescription = stringResource(R.string.more_options)
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = { PlainTooltip { Text(moreDescription) } },
                        state = rememberTooltipState(),
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                menuState.show {
                                    PlayerMenu(
                                        mediaMetadata = mediaMetadata,
                                        navController = navController,
                                        playerBottomSheetState = playerBottomSheetState,
                                        onShowDetailsDialog = {
                                            mediaMetadata?.id?.let {
                                                bottomSheetPageState.showFloating {
                                                    ShowMediaInfo(it)
                                                }
                                            }
                                        },
                                        onDismiss = menuState::dismiss
                                    )
                                }
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = chromePillContainer,
                                contentColor = chromePrimary,
                            )
                        ) {
                            SymbolIcon(symbol = sf("ellipsis"), contentDescription = moreDescription, modifier = Modifier
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                        }
                    }
                }
                }

                // Group the queue's playback toggles into equal-width pills.
                var keepPlaying by rememberPreference(AutoLoadMoreKey, defaultValue = true)
                var crossfadeOn by rememberPreference(CrossfadeEnabledKey, defaultValue = false)
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QueuePill(
                        checked = shuffleModeEnabled,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(
                                    if (shuffleModeEnabled) currentWindowIndex else 0,
                                )
                            }.invokeOnCompletion {
                                playerConnection.player.shuffleModeEnabled = checked
                            }
                        },
                        enabled = !isListenTogetherGuest,
                        icon = R.drawable.shuffle,
                        description = stringResource(R.string.shuffle),
                    )

                    QueuePill(
                        checked = repeatMode != Player.REPEAT_MODE_OFF,
                        onCheckedChange = { playerConnection.player.toggleRepeatMode() },
                        enabled = !isListenTogetherGuest,
                        icon = if (repeatMode == Player.REPEAT_MODE_ONE) R.drawable.repeat_one else R.drawable.repeat,
                        description = stringResource(R.string.repeat),
                    )

                    // Share the queue-end recommendation setting with Settings > Player.
                    QueuePill(
                        checked = keepPlaying,
                        onCheckedChange = { keepPlaying = it },
                        enabled = !isListenTogetherGuest,
                        icon = R.drawable.ic_queue_infinity,
                        description = stringResource(R.string.queue_keep_playing),
                    )

                    // MusicService reads this preference to control crossfades.
                    QueuePill(
                        checked = crossfadeOn,
                        onCheckedChange = { crossfadeOn = it },
                        enabled = !isListenTogetherGuest,
                        icon = R.drawable.ic_queue_crossfade,
                        description = stringResource(R.string.queue_crossfade),
                    )

                }


                // Show the queue source beneath the "Playing Next" heading.
                val playingNextSource by playerConnection.queueTitle.collectAsStateWithLifecycle()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.queue_playing_next),
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                            letterSpacing = (-0.43).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = chromePrimary,
                            maxLines = 1,
                        )
                        playingNextSource?.takeIf { it.isNotBlank() }?.let { source ->
                            Text(
                                text = stringResource(R.string.queue_playing_from, source),
                                fontSize = 17.sp,
                                lineHeight = 22.sp,
                                letterSpacing = (-0.43).sp,
                                color = QueueVibrantSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.additiveChrome(chromeAdditive),
                            )
                        }
                    }

                    if (!isListenTogetherGuest) {
                        Text(
                            text = stringResource(R.string.queue_clear),
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                            letterSpacing = (-0.43).sp,
                            color = QueueVibrantSecondary,
                            modifier = Modifier
                                .additiveChrome(chromeAdditive)
                                .clickable {
                                    val player = playerConnection.player
                                    val from = player.currentMediaItemIndex + 1
                                    if (from < player.mediaItemCount) player.removeMediaItems(from, player.mediaItemCount)
                                }
                                .padding(start = 12.dp),
                        )
                    }

                    // The header row's lock toggle only exists when showHeaderRow hid it —
                    // this is that same control's one home instead, not a second copy.
                    if (!showHeaderRow) {
                        val lockDescription = if (locked) stringResource(R.string.unlock_queue) else stringResource(R.string.lock_queue)
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                            tooltip = { PlainTooltip { Text(lockDescription) } },
                            state = rememberTooltipState(),
                        ) {
                            FilledTonalIconButton(
                                onClick = { locked = !locked },
                                modifier = Modifier.padding(start = 8.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = chromePillContainer,
                                    contentColor = chromePrimary,
                                )
                            ) {
                                SymbolIcon(symbol = if (locked) sf("lock.fill") else sf("lock.open.fill"), contentDescription = lockDescription, modifier = Modifier
                                        .additiveChrome(chromeAdditive)
                                        .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = inSelectMode,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                ) {
                    val selectedSongs = remember(selection.toList(), mutableQueueWindows) {
                        mutableQueueWindows.filter { it.mediaItem.mediaId in selection }
                            .mapNotNull { it.mediaItem.metadata }
                    }
                    val selectedItems = remember(selection.toList(), mutableQueueWindows) {
                        mutableQueueWindows.filter { it.mediaItem.mediaId in selection }
                    }
                    val count = selection.size
                    Row(
                        modifier =
                        Modifier
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = onExitSelectionMode,
                        ) {
                            SymbolIcon(symbol = sf("xmark"), contentDescription = null, modifier = Modifier
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                        }
                        Text(
                            text = pluralStringResource(R.plurals.n_selected, count, count),
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = count == mutableQueueWindows.size && count > 0,
                            onCheckedChange = {
                                if (count == mutableQueueWindows.size) {
                                    selection.clear()
                                } else {
                                    selection.clear()
                                    mutableQueueWindows.forEach {
                                        selection.add(it.mediaItem.mediaId)
                                    }
                                }
                            }
                        )
                        IconButton(
                            enabled = count > 0,
                            onClick = {
                                menuState.show {
                                    SelectionMediaMetadataMenu(
                                        songSelection = selectedSongs,
                                        onDismiss = menuState::dismiss,
                                        clearAction = onExitSelectionMode,
                                        currentItems = selectedItems,
                                    )
                                }
                            },
                        ) {
                            SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, modifier = Modifier
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = lazyListState,
                    contentPadding =
                        WindowInsets.systemBars
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
                            .add(
                                WindowInsets(
                                    top = 8.dp,
                                    bottom = ListItemHeight + 8.dp,
                                ),
                            ).asPaddingValues(),
                    modifier = Modifier
                        .fillMaxSize()
                        // Fade queue rows at the header and timeline edges.
                        .fadingEdge(vertical = 24.dp)
                        // Only install sheet drag handling when the parent does not own queue scrolling.
                        .then(
                            if (sheetDraggable) Modifier.nestedScroll(state.preUpPostDownNestedScrollConnection)
                            else Modifier
                        )
                ) {
                    item(key = "queue_top_spacer") {
                        Spacer(
                            modifier =
                                Modifier
                                    .animateContentSize()
                                    .height(if (inSelectMode) 48.dp else 0.dp),
                        )
                    }

                    itemsIndexed(
                        items = mutableQueueWindows,
                        // Compose saveable keys must be Bundle-compatible; uid.toString() preserves identity.
                        key = { _, item -> item.uid.toString() },
                    ) { index, window ->
                        ReorderableItem(
                            state = reorderableState,
                            key = window.uid.toString(),
                        ) {
                            val currentItem by rememberUpdatedState(window)
                            val isActive = window.uid == currentPlayingUid
                            val dismissBoxState =
                                rememberSwipeToDismissBoxState(
                                    positionalThreshold = { totalDistance -> totalDistance }
                                )

                            var processedDismiss by remember { mutableStateOf(false) }
                            LaunchedEffect(dismissBoxState.currentValue) {
                                val dv = dismissBoxState.currentValue
                                if (!processedDismiss && !isListenTogetherGuest && (
                                            dv == SwipeToDismissBoxValue.StartToEnd ||
                                                    dv == SwipeToDismissBoxValue.EndToStart
                                            )
                                ) {
                                    processedDismiss = true
                                    playerConnection.player.removeMediaItem(currentItem.firstPeriodIndex)
                                    dismissJob?.cancel()
                                    dismissJob = coroutineScope.launch {
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = context.getString(
                                                R.string.removed_song_from_playlist,
                                                currentItem.mediaItem.metadata?.title,

                                            ),
                                            actionLabel = context.getString(R.string.undo),
                                            duration = SnackbarDuration.Short,
                                        )
                                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                                            playerConnection.player.addMediaItem(currentItem.mediaItem)
                                            playerConnection.player.moveMediaItem(
                                                mutableQueueWindows.size,
                                                currentItem.firstPeriodIndex,
                                            )
                                        }
                                    }
                                }
                                if (dv == SwipeToDismissBoxValue.Settled) {
                                    processedDismiss = false
                                }
                            }

                            val onCheckedChange: (Boolean) -> Unit = {
                                if (it) {
                                    selection.add(window.mediaItem.mediaId)
                                } else {
                                    selection.remove(window.mediaItem.mediaId)
                                }
                            }

                            val content: @Composable () -> Unit = {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.animateItem(),
                                ) {
                                    MediaMetadataListItem(
                                        mediaMetadata = window.mediaItem.metadata!!,
                                        isSelected = false,
                                        // Artist + length use the same contrast-safe tier as the header subtitle.
                                        secondaryAdditiveChrome = false,
                                        subtitleColor = chromeArtist,
                                        titleAdditiveChrome = false,
                                        rowHeight = 64.dp, gutter = 30.dp, divider = false,
                                        isActive = isActive,
                                        isPlaying = isPlaying && isActive,
                                        shape = listItemShape(index, mutableQueueWindows.size),
                                        flat = true,
                                        trailingContent = {
                                            if (inSelectMode) {
                                                Checkbox(
                                                    checked = window.mediaItem.mediaId in selection,
                                                    onCheckedChange = onCheckedChange
                                                )
                                            } else {
                                                if (!isListenTogetherGuest) {
                                                    IconButton(
                                                        onClick = {
                                                            menuState.show {
                                                                QueueMenu(
                                                                    mediaMetadata = window.mediaItem.metadata!!,
                                                                    navController = navController,
                                                                    playerBottomSheetState = playerBottomSheetState,
                                                                    onShowDetailsDialog = {
                                                                        window.mediaItem.mediaId.let {
                                                                            bottomSheetPageState.showFloating {
                                                                                ShowMediaInfo(it)
                                                                            }
                                                                        }
                                                                    },
                                                                    onDismiss = menuState::dismiss,
                                                                )
                                                            }
                                                        }
                                                    ) {
                                                        SymbolIcon(symbol = sf("ellipsis"), contentDescription = null, modifier = Modifier
                                                                .additiveChrome(chromeAdditive)
                                                                .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                                                    }
                                                }
                                                if (!locked && !isListenTogetherGuest) {
                                                    IconButton(
                                                        onClick = { },
                                                        modifier = Modifier.draggableHandle()
                                                    ) {
                                                        SymbolIcon(symbol = sf("line.3.horizontal"), contentDescription = null, modifier = Modifier
                                                                .additiveChrome(chromeAdditive)
                                                                .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                                                    }
                                                }
                                            }
                                        },
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .background(background)
                                                .combinedClickable(
                                                    onClick = {
                                                        if (inSelectMode) {
                                                            onCheckedChange(window.mediaItem.mediaId !in selection)
                                                        } else if (!isListenTogetherGuest) {
                                                            if (index == currentWindowIndex) {
                                                                if (isCasting) {
                                                                    if (castIsPlaying) {
                                                                        castHandler?.pause()
                                                                    } else {
                                                                        castHandler?.play()
                                                                    }
                                                                } else {
                                                                    playerConnection.togglePlayPause()
                                                                }
                                                            } else {
                                                                if (isCasting) {
                                                                    val mediaId =
                                                                        window.mediaItem.mediaId
                                                                    val navigated =
                                                                        castHandler?.navigateToMediaIfInQueue(
                                                                            mediaId
                                                                        ) ?: false
                                                                    if (!navigated) {
                                                                        playerConnection.player.seekToDefaultPosition(
                                                                            window.firstPeriodIndex
                                                                        )
                                                                    }
                                                                } else {
                                                                    playerConnection.player.seekToDefaultPosition(
                                                                        window.firstPeriodIndex,
                                                                    )
                                                                    playerConnection.player.playWhenReady =
                                                                        true
                                                                }
                                                            }
                                                        }
                                                    },
                                                    onLongClick = {
                                                        if (!inSelectMode) {
                                                            haptic.performHapticFeedback(
                                                                HapticFeedbackType.LongPress
                                                            )
                                                            inSelectMode = true
                                                            onCheckedChange(true)
                                                        }
                                                    },
                                                ),
                                    )
                                }
                            }

                            if (locked) {
                                content()
                            } else {
                                SwipeToDismissBox(
                                    state = dismissBoxState,
                                    backgroundContent = {},
                                ) {
                                    content()
                                }
                            }
                        }
                    }

                    if (automix.isNotEmpty()) {
                        item(key = "automix_divider") {

                            Text(
                                text = stringResource(R.string.similar_content),
                                color = chromeSecondary,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .additiveChrome(chromeAdditive)
                                    .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha },
                            )
                        }

                        itemsIndexed(
                            items = automix,
                            key = { _, it -> it.mediaId },
                        ) { index, item ->
                            Row(
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                MediaMetadataListItem(
                                    mediaMetadata = item.metadata!!,
                                    shape = listItemShape(index, automix.size),
                                    secondaryAdditiveChrome = false,
                                    subtitleColor = chromeArtist,
                                    titleAdditiveChrome = false,
                                    rowHeight = 64.dp, gutter = 30.dp, divider = false,
                                    flat = true,
                                    trailingContent = {
                                        if (!isListenTogetherGuest) {
                                            IconButton(
                                                onClick = {
                                                    playerConnection.service.playNextAutomix(
                                                        item,
                                                        index,
                                                    )
                                                },
                                            ) {
                                                SymbolIcon(symbol = sf("text.line.first.and.arrowtriangle.forward"), contentDescription = null, modifier = Modifier
                                                        .additiveChrome(chromeAdditive)
                                                        .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha }, tint = chromePrimary)
                                            }
                                            IconButton(
                                                onClick = {
                                                    playerConnection.service.addToQueueAutomix(
                                                        item,
                                                        index,
                                                    )
                                                },
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.flamingo_lyrics),
                                                    contentDescription = null,
                                                    tint = chromePrimary,
                                                    modifier = Modifier
                                                        .additiveChrome(chromeAdditive)
                                                        .graphicsLayer { if (chromeAdditive) alpha = AdditiveChromeContentAlpha },
                                                )
                                            }
                                        }
                                    },
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = {},
                                                onLongClick = {
                                                    menuState.show {
                                                        QueueMenu(
                                                            mediaMetadata = item.metadata!!,
                                                            navController = navController,
                                                            playerBottomSheetState = playerBottomSheetState,
                                                            onShowDetailsDialog = {
                                                                item.mediaId.let {
                                                                    bottomSheetPageState.showFloating {
                                                                        ShowMediaInfo(it)
                                                                    }
                                                                }
                                                            },
                                                            onDismiss = menuState::dismiss,
                                                        )
                                                    }
                                                },
                                            )
                                            .animateItem(),
                                )
                            }
                        }
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier =
                    Modifier
                        .padding(
                            bottom =
                            ListItemHeight +
                                    WindowInsets.systemBars
                                        .asPaddingValues()
                                        .calculateBottomPadding(),
                        )
                        .align(Alignment.BottomCenter),
                )
            }
        }
    }

    if (showCommentSheet) {
        CommentSheet(
            videoId = mediaMetadata?.id ?: "",
            onDismiss = { showCommentSheet = false }
        )
    }
}

@Composable
private fun PlayerQueueButton(
    icon: Int,
    iconSlot: com.convx.music.ui.player.customize.PlayerIconSlot? = null,
    activeIcon: Int? = null,
    onClick: () -> Unit,
    isActive: Boolean,
    enabled: Boolean = true,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    text: String? = null,
    textButtonColor: Color,
    iconButtonColor: Color,
    iconSize: androidx.compose.ui.unit.Dp,
    textBackgroundColor: Color,
    playerBackground: PlayerBackgroundStyle
) {
    // Use fixed Flamingo colors; keep the theme parameters for existing callers.
    val chromeWhite = Color.White
    val chromeInactive = Color.White.copy(alpha = 0.35f)
    val chromeActiveFill = Color.White.copy(alpha = 0.28f)
    val chromeContentAlpha = 0.45f
    val buttonModifier = Modifier
        .clip(shape)
        .clickable(enabled = enabled, onClick = onClick)

    val alphaFactor = if (enabled) 1f else 0.35f

    val appliedModifier = if (isActive) {
        modifier.then(buttonModifier.background(chromeActiveFill)).alpha(alphaFactor)
    } else {
        modifier.then(
            buttonModifier.border(
                width = 1.dp,
                color = chromeWhite.copy(alpha = 0.3f),
                shape = shape
            )
        ).alpha(alphaFactor)
    }

    Box(
        modifier = appliedModifier,
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                color = (if (isActive) chromeWhite else chromeInactive).copy(alpha = if (enabled) 1f else 0.6f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .basicMarquee()
                    .additiveChrome(true)
                    .graphicsLayer { alpha = chromeContentAlpha }
            )
        } else {
            val baseTint = if (isActive) chromeWhite else chromeInactive
            val finalTint = if (enabled) baseTint else baseTint.copy(alpha = 0.5f)
            val resolvedIcon = if (isActive && activeIcon != null) activeIcon else icon
            if (iconSlot != null) {
                com.convx.music.ui.player.customize.PlayerGlyph(
                    slot = iconSlot,
                    fallbackSymbol = sf(if (iconSlot == com.convx.music.ui.player.customize.PlayerIconSlot.V2_QUEUE) "list.bullet" else "quote.bubble"),
                    size = iconSize,
                    bounce = true,
                    contentDescription = stringResource(if (iconSlot == com.convx.music.ui.player.customize.PlayerIconSlot.V2_QUEUE) R.string.queue else R.string.lyrics),
                    modifier = Modifier.additiveChrome(true).graphicsLayer { alpha = chromeContentAlpha },
                    tint = finalTint,
                )
            } else Icon(
                painter = painterResource(id = resolvedIcon),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .additiveChrome(true)
                    .graphicsLayer { alpha = chromeContentAlpha },
                tint = finalTint
            )
        }
    }
}

/** One icon-only pill of the queue's toggle group; equal width, so the row reads as one control. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowScope.QueuePill(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    icon: Int,
    description: String,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .clip(ContinuousRoundedRectangle(percent = 50))
            .graphicsLayer { if (checked) compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
            .background(if (checked) QueuePillActiveFill else QueuePillFill)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = androidx.compose.ui.semantics.Role.Switch,
                onValueChange = onCheckedChange,
            )
            .semantics { contentDescription = description },
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (checked) Color.Black else Color.White,
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer { if (checked) blendMode = androidx.compose.ui.graphics.BlendMode.DstOut },
        )
    }
}

/** Secondary color for additive labels such as "Clear" and "From". */
private val QueueVibrantSecondary = Color(0x4DEBEBF5)
private val QueuePillFill = Color(0x3D787880)
private val QueuePillActiveFill = Color(0xF2FFFFFF)
