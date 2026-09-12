/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app.ui.screens.settings.diy

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.nexamusic.app.R
import com.nexamusic.app.constants.DiyLayoutKey
import com.nexamusic.app.constants.PureBlackKey
import com.nexamusic.app.ui.component.BottomSheetState
import com.nexamusic.app.ui.component.LocalAppBackdrop
import com.nexamusic.app.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.nexamusic.app.ui.component.expandedAnchor
import com.nexamusic.app.ui.component.rememberBottomSheetState
import com.nexamusic.app.ui.player.BottomSheetPlayer
import com.nexamusic.app.ui.player.customize.DIY_MAX_ASSET_BYTES
import com.nexamusic.app.ui.player.customize.DIY_MAX_STICKERS
import com.nexamusic.app.ui.player.customize.DiyBounds
import com.nexamusic.app.ui.player.customize.DiyDesignCanvas
import com.nexamusic.app.ui.player.customize.DiyLayout
import com.nexamusic.app.ui.player.customize.DiyOrientation
import com.nexamusic.app.ui.player.customize.DiySticker
import com.nexamusic.app.ui.player.customize.DiyStickerContent
import com.nexamusic.app.ui.player.customize.DiyStickerKind
import com.nexamusic.app.ui.player.customize.DiyStore
import com.nexamusic.app.ui.player.customize.DiyTransform
import com.nexamusic.app.utils.MediaImport
import com.nexamusic.app.utils.dataStore
import com.nexamusic.app.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.min
import kotlin.math.roundToInt

/** How many steps back the editor remembers. Deep enough to undo a bad drag, not a whole session. */
private const val UNDO_DEPTH = 30

private val EMOJI_PALETTE = listOf(
    "✨", "🔥", "💜", "🌙", "⭐", "🎧", "🎵", "🌸", "🦋", "🍀",
    "☁️", "⚡", "🖤", "🌊", "🪩", "🎸", "💫", "🫧", "🌺", "🍄",
    "👑", "🍒", "🐾", "🎀", "🧊", "🌈", "☀️", "🪐", "🎐", "🕯️",
)

/**
 * Samsung-HomeUp-style layout editor for the player.
 *
 * The mockup fills the whole screen at the size the real player will be — nothing is shrunk into a
 * card — so a sticker placed here lands exactly where it looked like it would. Every control
 * floats over that canvas as a pill rather than taking a strip of it, for the same reason.
 */
@Composable
fun DiyEditorScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var layout by remember { mutableStateOf(DiyStore.load(context)) }
    // Follows the device's real rotation rather than a manual toggle — each
    // sticker already keeps a separate portrait/landscape transform (DiyLayout.kt's
    // DiySticker.portrait/landscape), so rotating the phone alone is enough to
    // preview and edit both layouts.
    val orientation = if (LocalConfiguration.current.orientation ==
        android.content.res.Configuration.ORIENTATION_LANDSCAPE
    ) DiyOrientation.LANDSCAPE else DiyOrientation.PORTRAIT
    var selectedId by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var emojiOpen by remember { mutableStateOf(false) }
    var editOpen by remember { mutableStateOf(false) }

    LaunchedEffect(orientation) {
        selectedId = null
        emojiOpen = false
        editOpen = false
    }

    val undoStack = remember { mutableStateListOf<DiyLayout>() }
    val redoStack = remember { mutableStateListOf<DiyLayout>() }

    /** Records the pre-change layout so the edit can be undone, then applies [next]. */
    fun commit(next: DiyLayout) {
        undoStack.add(layout)
        if (undoStack.size > UNDO_DEPTH) undoStack.removeAt(0)
        redoStack.clear()
        layout = next
    }

    /** Live drags mutate without an undo entry per frame; the gesture start pushes one. */
    fun replaceSelected(transform: (DiySticker) -> DiySticker) {
        val id = selectedId ?: return
        layout = DiyLayout(layout.stickers.map { if (it.id == id) transform(it) else it })
    }

    val selected = layout.stickers.firstOrNull { it.id == selectedId }
    val tooManyMessage = stringResource(R.string.diy_limit_reached, DIY_MAX_STICKERS)
    val budgetMessage = stringResource(R.string.diy_budget_reached)

    fun addSticker(sticker: DiySticker) {
        commit(DiyLayout(layout.stickers + sticker))
        selectedId = sticker.id
    }

    fun nextZ() = (layout.stickers.maxOfOrNull { it.z } ?: 0) + 1

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        if (layout.stickers.size >= DIY_MAX_STICKERS) {
            errorMessage = tooManyMessage
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val id = UUID.randomUUID().toString()
            val result = withContext(Dispatchers.IO) {
                if (DiyStore.assetBytes(context) >= DIY_MAX_ASSET_BYTES) null
                else MediaImport.import(
                    context = context,
                    uri = uri,
                    kind = MediaImport.Kind.STICKER,
                    destDir = DiyStore.stickerDir(context),
                    baseName = id,
                )
            }
            when (result) {
                null -> errorMessage = budgetMessage
                is MediaImport.Result.Failed ->
                    errorMessage = context.getString(importErrorRes(result.error))

                is MediaImport.Result.Ok -> addSticker(
                    DiySticker(
                        id = id,
                        kind = DiyStickerKind.IMAGE,
                        source = result.file.name,
                        z = nextZ(),
                    ),
                )
            }
        }
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text(stringResource(R.string.diy_cannot_add)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
        )
    }

    val (pureBlack) = rememberPreference(PureBlackKey, defaultValue = false)

    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black)) {
        // Forced permanently expanded, own state distinct from the real singleton
        // player's — this is a second, preview-only mount of the same composable
        // (see BottomSheetPlayer's isPreviewOnly doc), not the app's actual player.
        val editorPlayerState = rememberBottomSheetState(
            dismissedBound = 0.dp,
            expandedBound = maxHeight,
            collapsedBound = 0.dp,
            initialAnchor = expandedAnchor,
        )

        EditorCanvas(
            layout = layout,
            orientation = orientation,
            selectedId = selectedId,
            navController = navController,
            pureBlack = pureBlack,
            playerState = editorPlayerState,
            onSelect = {
                selectedId = it
                if (it == null) {
                    emojiOpen = false
                    editOpen = false
                }
            },
            onGestureStart = {
                undoStack.add(layout)
                if (undoStack.size > UNDO_DEPTH) undoStack.removeAt(0)
                redoStack.clear()
            },
            onTransform = { transform ->
                replaceSelected { it.withTransform(orientation, transform) }
            },
            modifier = Modifier.fillMaxSize(),
        )

        TopControls(
            canUndo = undoStack.isNotEmpty(),
            canRedo = redoStack.isNotEmpty(),
            onBack = navController::navigateUp,
            onUndo = {
                redoStack.add(layout)
                layout = undoStack.removeAt(undoStack.lastIndex)
                selectedId = null
                emojiOpen = false
                editOpen = false
            },
            onRedo = {
                undoStack.add(layout)
                layout = redoStack.removeAt(redoStack.lastIndex)
                selectedId = null
                emojiOpen = false
                editOpen = false
            },
            onSave = {
                scope.launch {
                    val saved = layout
                    context.dataStore.edit { it[DiyLayoutKey] = saved.toJson() }
                    withContext(Dispatchers.IO) { DiyStore.pruneOrphans(context, saved) }
                    Toast.makeText(
                        context,
                        context.getString(R.string.diy_saved),
                        Toast.LENGTH_SHORT,
                    ).show()
                    navController.navigateUp()
                }
            },
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
        )

        BottomControls(
            selected = selected,
            stickerCount = layout.stickers.size,
            emojiOpen = emojiOpen,
            editOpen = editOpen,
            onAddImage = { pickImageLauncher.launch(arrayOf("image/*", "image/svg+xml")) },
            onToggleEmoji = {
                emojiOpen = !emojiOpen
                editOpen = false
            },
            onToggleEdit = {
                editOpen = !editOpen
                emojiOpen = false
            },
            onDelete = {
                val id = selectedId
                if (id != null) {
                    commit(DiyLayout(layout.stickers.filterNot { it.id == id }))
                    selectedId = null
                    editOpen = false
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
        )

        PopupLayer(
            emojiOpen = emojiOpen,
            editOpen = editOpen,
            selected = selected,
            orientation = orientation,
            onDismiss = {
                emojiOpen = false
                editOpen = false
            },
            onPickEmoji = { emoji ->
                if (layout.stickers.size >= DIY_MAX_STICKERS) {
                    errorMessage = tooManyMessage
                } else {
                    addSticker(
                        DiySticker(
                            id = UUID.randomUUID().toString(),
                            kind = DiyStickerKind.EMOJI,
                            source = emoji,
                            z = nextZ(),
                        ),
                    )
                }
                emojiOpen = false
            },
            onChange = { updated ->
                commit(DiyLayout(layout.stickers.map { if (it.id == updated.id) updated else it }))
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

// ------------------------------------------------------------------------------- canvas

/**
 * The real player at true size (background, artwork, controls — not an approximation), plus
 * an interactive sticker layer over it.
 *
 * Hit-testing lives here rather than on the stickers themselves: one gesture surface picks the
 * topmost sticker under the finger, which handles overlaps correctly and keeps the shipped sticker
 * renderer input-free so it can be reused verbatim in the real player.
 */
@Composable
private fun EditorCanvas(
    layout: DiyLayout,
    orientation: DiyOrientation,
    selectedId: String?,
    navController: NavController,
    pureBlack: Boolean,
    playerState: BottomSheetState,
    onSelect: (String?) -> Unit,
    onGestureStart: () -> Unit,
    onTransform: (DiyTransform) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The gesture handlers must read the *current* sticker, not the one captured when the handler
    // was installed — otherwise every drag frame applies its pan to the original position and the
    // sticker barely moves.
    val liveLayout by rememberUpdatedState(layout)
    val liveSelectedId by rememberUpdatedState(selectedId)
    // Per-gesture state shared between the touch-down selector and the transform detector. Not read
    // during composition, so writing them never re-renders the canvas.
    val gestureTarget = remember { mutableStateOf<String?>(null) }
    val undoPending = remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // DiyEditorScreen is itself a NavHost destination, inside the root
        // appBackdrop's capture subtree — so BottomSheetPlayer's own glass base
        // (Player.kt's Modifier.liquidGlass on the `background` slot) must not
        // resolve LocalAppBackdrop to that root appBackdrop, or its capture
        // includes itself (RenderNode::prepareTreeImpl SIGSEGV, see
        // glass-chrome-rendernode-cycle). Provide an UNATTACHED screen-local
        // backdrop first, same pattern as AlbumScreen/ArtistScreen/DiyPlayerMockup.
        val editorBackdrop = rememberLayerBackdrop()
        CompositionLocalProvider(LocalAppBackdrop provides editorBackdrop) {
            BottomSheetPlayer(
                state = playerState,
                navController = navController,
                pureBlack = pureBlack,
                showDiyStickers = false,
                forcedOrientation = orientation,
                isPreviewOnly = true,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // ponytail: every sticker draws above the real player here regardless of its z —
        // there's no slot to inject "behind artwork, above background" into a composable
        // this doesn't own the internal stack of. The saved z still applies correctly once
        // played for real (showDiyStickers back on, outside the editor). Upgrade path: an
        // injectable content slot in BottomSheetPlayer at that exact point in its stack.
        DiyDesignCanvas(orientation = orientation, modifier = Modifier.fillMaxSize()) { scale ->
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val bounds = DiyBounds(maxWidth, maxHeight)
                layout.stickers
                    .sortedBy { it.z }
                    .forEach { sticker ->
                        DiyStickerContent(
                            sticker = sticker,
                            orientation = orientation,
                            bounds = bounds,
                            modifier = if (sticker.id == selectedId) {
                                Modifier.border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(4.dp),
                                )
                            } else {
                                Modifier
                            },
                        )
                    }
            }

            BoxWithConstraints(Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val widthPx = with(density) { maxWidth.toPx() }
                val heightPx = with(density) { maxHeight.toPx() }
                val referencePx = with(density) {
                    DiyBounds(maxWidth, maxHeight).referenceEdge.toPx()
                }
                // hitTest runs in this pre-scale design space, so a physical minimum touch
                // target (MIN_TOUCH_HALF_PX real px) needs to be grown by 1/scale here, or a
                // narrower design-to-screen scale (as landscape's tends to be) silently shrinks
                // the real on-screen grab radius for the exact same design-space constant.
                val minTouchHalfPx = MIN_TOUCH_HALF_PX / scale

                Box(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(orientation) {
                            detectTapGestures { offset ->
                                onSelect(
                                    hitTest(
                                        liveLayout, orientation, offset,
                                        widthPx, heightPx, referencePx, minTouchHalfPx,
                                    ),
                                )
                            }
                        }
                        .pointerInput(orientation) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val hit = hitTest(
                                    liveLayout, orientation, down.position,
                                    widthPx, heightPx, referencePx, minTouchHalfPx,
                                )
                                if (hit != null) onSelect(hit)
                                // A drag selects whichever sticker the finger landed on and moves it
                                // in the same gesture, so emoji and images are draggable directly.
                                // Grabbing empty space keeps moving what was already selected.
                                gestureTarget.value = hit ?: liveSelectedId
                                undoPending.value = true
                            }
                        }
                        .pointerInput(orientation) {
                            detectTransformGestures(panZoomLock = false) { _, pan, zoom, rotation ->
                                val id = gestureTarget.value ?: return@detectTransformGestures
                                val current = liveLayout.stickers.firstOrNull { it.id == id }
                                    ?.transformFor(orientation)
                                    ?: return@detectTransformGestures
                                if (undoPending.value) {
                                    onGestureStart()
                                    undoPending.value = false
                                }
                                onTransform(
                                    current.copy(
                                        x = (current.x + pan.x / widthPx).coerceIn(-0.2f, 1.2f),
                                        y = (current.y + pan.y / heightPx).coerceIn(-0.2f, 1.2f),
                                        scale = (current.scale * zoom).coerceIn(
                                            DiyTransform.MIN_SCALE,
                                            DiyTransform.MAX_SCALE,
                                        ),
                                        rotation = current.rotation + rotation,
                                    ),
                                )
                            }
                        },
                )
            }
        }
    }
}

/** @return the id of the topmost sticker whose box contains [offset], or null. */
private fun hitTest(
    layout: DiyLayout,
    orientation: DiyOrientation,
    offset: Offset,
    widthPx: Float,
    heightPx: Float,
    referencePx: Float,
    minTouchHalfPx: Float = MIN_TOUCH_HALF_PX,
): String? = layout.stickers
    .sortedByDescending { it.z }
    .firstOrNull { sticker ->
        val transform = sticker.transformFor(orientation)
        // Emoji and small images get a minimum grab area, so a tiny sticker is still catchable.
        val half = maxOf(referencePx * transform.scale / 2f, minTouchHalfPx)
        val cx = widthPx * transform.x
        val cy = heightPx * transform.y
        offset.x in (cx - half)..(cx + half) && offset.y in (cy - half)..(cy + half)
    }
    ?.id

private const val MIN_TOUCH_HALF_PX = 36f

// ------------------------------------------------------------------------ floating controls

@Composable
private fun TopControls(
    canUndo: Boolean,
    canRedo: Boolean,
    onBack: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Modifier.weight needs a bounded main-axis constraint, which
    // horizontalScroll below deliberately relaxes to unbounded — so the
    // left/right pill groups can no longer be split by a weighted Spacer.
    // Arrangement.SpaceBetween achieves the same "push apart" layout at rest
    // (it only needs the Row's own resolved width, computed after scroll
    // content is measured) and degrades to scrollable overflow instead of
    // clipping when the groups don't both fit.
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Pill {
                PillIcon(R.drawable.arrow_back, onClick = onBack)
            }
        }
        Pill {
            PillIcon(R.drawable.replay, enabled = canUndo, onClick = onUndo)
            PillIcon(R.drawable.fast_forward, enabled = canRedo, onClick = onRedo)
            PillIcon(R.drawable.check, onClick = onSave)
        }
    }
}

@Composable
private fun BottomControls(
    selected: DiySticker?,
    stickerCount: Int,
    emojiOpen: Boolean,
    editOpen: Boolean,
    onAddImage: () -> Unit,
    onToggleEmoji: () -> Unit,
    onToggleEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 16.dp)
            // The edit/delete pill only appears once a sticker is selected and
            // is laid out last, so it's the one most likely to overflow
            // off-screen on narrow widths — scroll rather than clip it.
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Pill {
            PillText(
                text = stringResource(R.string.diy_add_image),
                onClick = onAddImage,
            )
            PillText(
                text = stringResource(R.string.diy_add_emoji),
                selected = emojiOpen,
                onClick = onToggleEmoji,
            )
            PillText(text = "$stickerCount / $DIY_MAX_STICKERS", enabled = false, onClick = {})
        }

        if (selected != null) {
            Pill {
                PillText(
                    text = stringResource(R.string.diy_edit),
                    selected = editOpen,
                    onClick = onToggleEdit,
                )
                PillIcon(R.drawable.delete, tint = MaterialTheme.colorScheme.error, onClick = onDelete)
            }
        }
    }
}

/** A floating rounded container. Everything that would otherwise steal canvas space lives in one. */
@Composable
private fun Pill(content: @Composable () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
        tonalElevation = 3.dp,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) { content() }
    }
}

@Composable
private fun PillIcon(
    icon: Int,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (enabled) tint else tint.copy(alpha = 0.35f),
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun PillText(
    text: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        modifier = Modifier.clip(CircleShape).clickable(enabled = enabled, onClick = onClick),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = when {
                selected -> MaterialTheme.colorScheme.onPrimary
                enabled -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}

// ---------------------------------------------------------------------------- floating popups

/**
 * The popup layer. Drawn last so its panels float above the pills; input-inert when nothing is
 * open. Offsets are remembered here so a panel the user parked somewhere stays parked for the
 * rest of the session instead of snapping back on every reopen.
 */
@Composable
private fun PopupLayer(
    emojiOpen: Boolean,
    editOpen: Boolean,
    selected: DiySticker?,
    orientation: DiyOrientation,
    onDismiss: () -> Unit,
    onPickEmoji: (String) -> Unit,
    onChange: (DiySticker) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val maxX = with(density) { maxWidth.toPx() }
        val maxY = with(density) { maxHeight.toPx() }
        val lift = with(density) { (-180).dp.toPx().roundToInt() }
        var emojiOffset by remember { mutableStateOf(Offset(0f, lift.toFloat())) }
        var editOffset by remember { mutableStateOf(Offset(0f, lift.toFloat())) }

        if (emojiOpen || editOpen) {
            // Transparent tap-away scrim: closes the popup without hiding the canvas behind it.
            Box(
                Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures { onDismiss() }
                    },
            )
        }
        if (emojiOpen) {
            DraggablePanel(
                offset = emojiOffset,
                onOffsetChange = { emojiOffset = it },
                maxX = maxX,
                maxY = maxY,
            ) {
                EmojiPanel(onPick = onPickEmoji)
            }
        }
        if (editOpen && selected != null) {
            DraggablePanel(
                offset = editOffset,
                onOffsetChange = { editOffset = it },
                maxX = maxX,
                maxY = maxY,
            ) {
                StickerPanel(
                    sticker = selected,
                    orientation = orientation,
                    onChange = onChange,
                )
            }
        }
    }
}

/**
 * A floating panel dragged around by its body. Appears just above the bottom pill bar and follows
 * the finger wherever it is pulled; sliders, switches and buttons inside keep their own gestures,
 * so grabbing a control adjusts it and grabbing anything else moves the panel.
 */
@Composable
private fun BoxScope.DraggablePanel(
    offset: Offset,
    onOffsetChange: (Offset) -> Unit,
    maxX: Float,
    maxY: Float,
    content: @Composable () -> Unit,
) {
    val liveOffset by rememberUpdatedState(offset)
    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(maxX, maxY) {
                detectDragGestures { change, dragAmount ->
                    val current = liveOffset
                    onOffsetChange(
                        Offset(
                            (current.x + dragAmount.x).coerceIn(-maxX, maxX),
                            (current.y + dragAmount.y).coerceIn(-maxY, 0f),
                        ),
                    )
                }
            },
    ) {
        content()
    }
}

/** Rounded floating chrome shared by every popup, with a grab-handle bar up top. */
@Composable
private fun PopupPanel(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.96f),
        tonalElevation = 4.dp,
        shadowElevation = 12.dp,
    ) {
        Column {
            GrabHandle()
            content()
        }
    }
}

/** A little bar that signals the panel can be picked up and moved. */
@Composable
private fun GrabHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .width(36.dp)
                .height(5.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)),
        )
    }
}

@Composable
private fun EmojiPanel(onPick: (String) -> Unit) {
    PopupPanel {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.widthIn(max = 280.dp).height(200.dp).padding(horizontal = 12.dp),
        ) {
            items(EMOJI_PALETTE) { emoji ->
                Text(
                    text = emoji,
                    fontSize = 26.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onPick(emoji) }
                        .padding(6.dp),
                )
            }
        }
    }
}

/** Everything adjustable about one sticker, floating over the canvas rather than beside it. */
@Composable
private fun StickerPanel(
    sticker: DiySticker,
    orientation: DiyOrientation,
    onChange: (DiySticker) -> Unit,
) {
    PopupPanel {
        val transform = sticker.transformFor(orientation)
        Column(
            Modifier.widthIn(min = 260.dp, max = 300.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            LowResolutionHint(sticker, transform)

            MenuSlider(
                label = stringResource(R.string.diy_size),
                value = transform.scale,
                range = DiyTransform.MIN_SCALE..DiyTransform.MAX_SCALE,
                onChange = {
                    onChange(sticker.withTransform(orientation, transform.copy(scale = it)))
                },
            )
            MenuSlider(
                label = stringResource(R.string.diy_rotation),
                value = transform.rotation,
                range = -180f..180f,
                onChange = {
                    onChange(sticker.withTransform(orientation, transform.copy(rotation = it)))
                },
            )
            MenuSlider(
                label = stringResource(R.string.diy_opacity),
                value = sticker.opacity,
                range = 0.05f..1f,
                onChange = { onChange(sticker.copy(opacity = it)) },
            )
            if (sticker.kind == DiyStickerKind.IMAGE) {
                MenuSlider(
                    label = stringResource(R.string.diy_corner_radius),
                    value = sticker.cornerRadius,
                    range = 0f..0.5f,
                    onChange = { onChange(sticker.copy(cornerRadius = it)) },
                )
            }

            MenuSwitch(
                label = stringResource(R.string.diy_flip),
                checked = sticker.flipHorizontal,
                onChange = { onChange(sticker.copy(flipHorizontal = it)) },
            )
            MenuSwitch(
                label = stringResource(R.string.diy_shadow),
                checked = sticker.shadow,
                onChange = { onChange(sticker.copy(shadow = it)) },
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp),
            ) {
                PillText(
                    text = stringResource(R.string.diy_send_backward),
                    onClick = {
                        onChange(sticker.copy(z = (sticker.z - 1).coerceAtLeast(-DiySticker.MAX_Z)))
                    },
                )
                PillText(
                    text = stringResource(R.string.diy_bring_forward),
                    onClick = {
                        onChange(sticker.copy(z = (sticker.z + 1).coerceAtMost(DiySticker.MAX_Z)))
                    },
                )
            }
            Text(
                text = stringResource(
                    if (sticker.z < 0) R.string.diy_behind_artwork else R.string.diy_above_artwork,
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun MenuSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Slider(value = value.coerceIn(range), onValueChange = onChange, valueRange = range)
    }
}

@Composable
private fun MenuSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

/**
 * Warns when a sticker has been blown up past its own pixel density.
 *
 * A hint, not a limit: someone may genuinely want a chunky low-res sprite, and refusing to scale it
 * would be the editor overruling a deliberate choice.
 */
@Composable
private fun LowResolutionHint(sticker: DiySticker, transform: DiyTransform) {
    if (sticker.kind != DiyStickerKind.IMAGE) return
    val context = LocalContext.current
    val sourcePx = remember(sticker.source) {
        val file = DiyStore.stickerFile(context, sticker) ?: return@remember 0
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        min(bounds.outWidth, bounds.outHeight)
    }
    if (sourcePx <= 0) return

    val metrics = context.resources.displayMetrics
    val screenPx = min(metrics.widthPixels, metrics.heightPixels)
    if (screenPx * transform.scale <= sourcePx * 1.15f) return

    Text(
        text = stringResource(R.string.diy_low_resolution),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

internal fun importErrorRes(error: MediaImport.Error): Int = when (error) {
    MediaImport.Error.TOO_LARGE -> R.string.media_import_too_large
    MediaImport.Error.UNSUPPORTED_TYPE -> R.string.media_import_unsupported
    MediaImport.Error.UNSAFE_SVG -> R.string.media_import_unsafe_svg
    MediaImport.Error.CORRUPT -> R.string.media_import_corrupt
    MediaImport.Error.TOO_MANY_PIXELS -> R.string.media_import_too_many_pixels
    MediaImport.Error.IO -> R.string.media_import_io
}
