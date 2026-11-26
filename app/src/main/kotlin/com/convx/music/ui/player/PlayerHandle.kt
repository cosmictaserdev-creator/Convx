// Adapted from Mochy Music PlayerHandle.kt, GPL-3.0. See NOTICE.

package com.convx.music.ui.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.Window
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.convx.music.ui.component.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** The handle's opacity while the player is being touched, and once it has been left alone. */
private const val HandleActiveAlpha = 0.6f
private const val HandleIdleAlpha = 0.15f

/** How long the player has to be left alone before the handle goes faint. */
private const val HandleIdleDelayMs = 4_000L

/** The bar: its length end to end (round caps included), its thickness, and its centre below the status bar. */
private val HandleLength = 100.dp
private val HandleThickness = 5.dp
private val HandleCentreBelowStatusBar = 10.dp

/** The chevron it bends into: end to end, and how far its point drops below its ends. */
private val ChevronLength = 44.dp
private val ChevronDepth = 10.dp

/** Where a tap on it lands: wider and taller than the bar itself, centred on it. */
private val HandleTargetWidth = 150.dp
private val HandleTargetHeight = 28.dp

/** How far open the sheet has to look for a tap on the handle to close it (the mini player sits at 0). */
private const val HandleTapMinProgress = 0.9f

/** How often the colour behind it is looked at again while the player is open. */
private const val HandleSampleIntervalMs = 1_500L

/** The strip just under the bar that's sampled for its colour: how far below the bar's centre, and how tall. */
private val SampleBelowCentre = 6.dp
private val SampleHeight = 10.dp
private const val SampleBitmapWidth = 24
private const val SampleBitmapHeight = 4

private val HandleWakeSpec = tween<Float>(durationMillis = 220, easing = FastOutSlowInEasing)
private val HandleFadeSpec = tween<Float>(durationMillis = 900, easing = FastOutSlowInEasing)
private val HandleBendSpring = spring<Float>(dampingRatio = 0.82f, stiffness = 420f)

/**
 * The handle's brightness: up while the player is touched, faint once it's been left alone for
 * [HandleIdleDelayMs]. Fed by the sheet's touches ([onTouchActivity]).
 */
@Stable
class PlayerHandleState internal constructor(private val scope: CoroutineScope) {
    internal val alpha = Animatable(HandleActiveAlpha)
    private var idleJob: Job? = null

    /** The player was touched (true), or the last finger left it (false). */
    fun onTouchActivity(touching: Boolean) {
        if (touching) {
            idleJob?.cancel()
            idleJob = null
            scope.launch { alpha.animateTo(HandleActiveAlpha, HandleWakeSpec) }
        } else {
            restartIdleWait()
        }
    }

    /** Bright, with a fresh wait before it fades: the player has just opened. */
    internal fun wake() {
        scope.launch { alpha.animateTo(HandleActiveAlpha, HandleWakeSpec) }
        restartIdleWait()
    }

    private fun restartIdleWait() {
        idleJob?.cancel()
        idleJob = scope.launch {
            delay(HandleIdleDelayMs)
            alpha.animateTo(HandleIdleAlpha, HandleFadeSpec)
        }
    }
}

@Composable
fun rememberPlayerHandleState(): PlayerHandleState {
    val scope = rememberCoroutineScope()
    return remember(scope) { PlayerHandleState(scope) }
}

/**
 * The open player's handle, at the top of every style: a bar that says the player is a sheet to
 * pull down. 60% while the player is touched, 15% once it's been left alone, black or white -
 * whichever reads over what's behind it. A tap on it closes the player; pressed, and while the
 * player closes, it bends into a chevron pointing down.
 *
 * Every per-frame value (the bend, the brightness, the colour's fade) is read while drawing, so
 * none of it recomposes anything.
 */
@Composable
fun PlayerHandle(
    state: PlayerHandleState,
    sheet: BottomSheetState,
    statusBarTop: Dp,
    /** False while something else owns the top of the player (the studio). */
    enabled: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    val currentEnabled by rememberUpdatedState(enabled)
    val bend = remember { Animatable(0f) }
    var pressed by remember { mutableStateOf(false) }

    // A chevron while pressed and while the player is closing; flat again once it's down, so the
    // next open starts from the bar.
    LaunchedEffect(sheet) {
        snapshotFlow { pressed || ((sheet.isDragging || !sheet.targetOpen) && sheet.progress > 0f && sheet.progress < 1f) }
            .collectLatest { chevron -> bend.animateTo(if (chevron) 1f else 0f, HandleBendSpring) }
    }
    LaunchedEffect(sheet, state) {
        snapshotFlow { sheet.targetOpen || sheet.isExpanded }.collectLatest { open -> if (open) state.wake() }
    }

    // Window bounds of the tap target, for the colour sampling. Not state: only read when sampling.
    val targetBounds = remember { FloatArray(4) }
    val onLight by rememberOnLightBehind(sheet, targetBounds)
    val color by animateColorAsState(
        targetValue = if (onLight) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 300),
        label = "playerHandleColor",
    )

    Box(
        modifier = modifier
            .offset(y = statusBarTop + HandleCentreBelowStatusBar - HandleTargetHeight / 2)
            .size(HandleTargetWidth, HandleTargetHeight)
            .onGloballyPositioned {
                val bounds = it.boundsInWindow()
                targetBounds[0] = bounds.left
                targetBounds[1] = bounds.top
                targetBounds[2] = bounds.right
                targetBounds[3] = bounds.bottom
            }
            .pointerInput(sheet) {
                awaitEachGesture {
                    awaitFirstDown()
                    // Only on the open player: collapsed, this spot is inside the mini player,
                    // whose tap opens it. The down isn't consumed, so a drag from here still
                    // reaches the sheet and closes it.
                    // Gated on how open the sheet looks, not on where it was last sent: a player
                    // that shows open while its anchor says otherwise (a cut-short animation) has
                    // to stay closable from here - the handle is the one way out that's always
                    // on screen (#109).
                    if (!currentEnabled() || sheet.progress < HandleTapMinProgress) {
                        return@awaitEachGesture
                    }
                    pressed = true
                    val up = waitForUpOrCancellation()
                    pressed = false
                    if (up != null) {
                        up.consume()
                        sheet.collapseSoft()
                    }
                }
            }
            .graphicsLayer {
                alpha = if (currentEnabled()) state.alpha.value else 0f
                // one stroke: its alpha can go on the paint, with no layer of its own
                compositingStrategy = CompositingStrategy.ModulateAlpha
            }
            .drawWithCache {
                val path = Path()
                val stroke = Stroke(
                    width = HandleThickness.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                )
                // Half the length between the cap centres, for the bar and for the chevron.
                val barHalf = (HandleLength - HandleThickness).toPx() / 2f
                val chevronHalf = (ChevronLength - HandleThickness).toPx() / 2f
                val depthPx = ChevronDepth.toPx()
                onDrawBehind {
                    val b = bend.value
                    val half = lerp(barHalf, chevronHalf, b)
                    val depth = depthPx * b
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    path.reset()
                    path.moveTo(cx - half, cy - depth / 2f)
                    path.lineTo(cx, cy + depth / 2f)
                    path.lineTo(cx + half, cy - depth / 2f)
                    drawPath(path = path, color = color, style = stroke)
                }
            },
    )
}

/**
 * Whether what's behind the handle is light, so the handle should be black. Read off the window
 * itself - the one way that holds for every style, cover, canvas and page - from a small strip just
 * under the bar, while the player sits open, every [HandleSampleIntervalMs]. The copy is a few dozen
 * pixels, made on the render thread.
 */
@Composable
private fun rememberOnLightBehind(sheet: BottomSheetState, targetBounds: FloatArray): State<Boolean> {
    val onLight = remember { mutableStateOf(false) }
    val view = LocalView.current
    val density = LocalDensity.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(view, sheet, density) {
        val window = view.context.findHandleActivity()?.window ?: return@LaunchedEffect
        val bitmap = Bitmap.createBitmap(SampleBitmapWidth, SampleBitmapHeight, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(SampleBitmapWidth * SampleBitmapHeight)
        val handler = Handler(Looper.getMainLooper())
        val halfBar = with(density) { HandleLength.toPx() } / 2f
        val below = with(density) { SampleBelowCentre.toPx() }
        val height = with(density) { SampleHeight.toPx() }
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            snapshotFlow { sheet.isExpanded }.collectLatest { open ->
                if (!open) return@collectLatest
                while (true) {
                    val cx = (targetBounds[0] + targetBounds[2]) / 2f
                    val cy = (targetBounds[1] + targetBounds[3]) / 2f
                    val rect = android.graphics.Rect(
                        (cx - halfBar).toInt(),
                        (cy + below).toInt(),
                        (cx + halfBar).toInt(),
                        (cy + below + height).toInt(),
                    )
                    if (rect.width() > 0 && rect.height() > 0 && copyWindow(window, rect, bitmap, handler)) {
                        bitmap.getPixels(pixels, 0, SampleBitmapWidth, 0, 0, SampleBitmapWidth, SampleBitmapHeight)
                        var sum = 0f
                        for (c in pixels) {
                            sum += 0.299f * ((c shr 16) and 0xFF) + 0.587f * ((c shr 8) and 0xFF) + 0.114f * (c and 0xFF)
                        }
                        val luminance = sum / (pixels.size * 255f)
                        // Apart, so a backdrop sitting near the line doesn't flip it back and forth.
                        onLight.value = if (onLight.value) luminance > 0.52f else luminance > 0.66f
                    }
                    delay(HandleSampleIntervalMs)
                }
            }
        }
    }
    return onLight
}

private suspend fun copyWindow(window: Window, rect: android.graphics.Rect, bitmap: Bitmap, handler: Handler): Boolean =
    suspendCancellableCoroutine { continuation ->
        try {
            PixelCopy.request(
                window,
                rect,
                bitmap,
                { result -> if (continuation.isActive) continuation.resume(result == PixelCopy.SUCCESS) },
                handler,
            )
        } catch (_: IllegalArgumentException) {
            // no surface to copy from yet (or any more)
            if (continuation.isActive) continuation.resume(false)
        }
    }

internal tailrec fun Context.findHandleActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findHandleActivity()
    else -> null
}
