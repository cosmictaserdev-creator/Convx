package com.convx.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.ui.player.backdrop.BackdropAnchor
import com.convx.music.ui.player.backdrop.ExtendedPlayerBackdrop

/** The styles supply their own content and backdrop anchor. */
@Composable
internal fun PlayerHost(
    controller: CanvasVideoController,
    anchor: BackdropAnchor,
    modifier: Modifier = Modifier,
    lyricsVisible: Boolean = false,
    showCanvasFeedback: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val track = controller.trackState.collectAsStateWithLifecycle()
    Box(modifier) {
        ExtendedPlayerBackdrop(
            backdrop = controller.backdrop,
            anchor = anchor,
            extended = true,
            modifier = Modifier.matchParentSize().graphicsLayer {
                alpha = if (controller.canDraw(controller.loadGeneration, track.value)) 1f else 0f
            },
        )
        content()
        if (lyricsVisible) PlayerLyricsHeaderBand(Modifier.align(Alignment.TopCenter))
        val toggle = controller.canvasToggle
        val session = controller.session
        if (showCanvasFeedback && toggle != null && session.track == track.value) {
            CanvasFeedback(
                phase = session.phase,
                mode = controller.canvasMode,
                overrideEnabled = controller.canvasOverride,
                epoch = session.track.epoch,
                onSetEnabled = toggle,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
            )
        }
    }
}

@Composable
internal fun rememberQueueCoverDrag(
    sheet: com.convx.music.ui.component.BottomSheetState,
    onOpenQueue: () -> Unit,
): QueueCoverDrag {
    val threshold = with(androidx.compose.ui.platform.LocalDensity.current) { 80.dp.toPx() }
    val open = androidx.compose.runtime.rememberUpdatedState(onOpenQueue)
    return androidx.compose.runtime.remember(sheet, threshold) { QueueCoverDrag(sheet, threshold) { open.value() } }
}

internal class QueueCoverDrag(
    private val sheet: com.convx.music.ui.component.BottomSheetState,
    private val threshold: Float,
    private val openQueue: () -> Unit,
) {
    private var collapsing = false

    fun move(delta: Float, displacement: Float) {
        if (displacement <= 0f && !collapsing) return
        sheet.dragClockNs[0] = System.nanoTime()
        if (collapsing) {
            sheet.dispatchRawDelta(delta)
        } else {
            collapsing = true
            sheet.onDragStart(fromMiniPlayer = false)
            sheet.dispatchRawDelta(displacement)
        }
    }

    fun finish(displacement: Float, velocity: Float) {
        if (collapsing) {
            sheet.dragClockNs[0] = 0L
            sheet.performFling(-velocity, onDismiss = null)
            collapsing = false
        } else if (displacement <= -threshold) {
            openQueue()
        }
    }

    fun cancel() = finish(0f, 0f)
}

@Composable
internal fun rememberDragToQueueModifier(
    enabled: Boolean,
    state: com.convx.music.ui.component.BottomSheetState,
    onOpenQueue: () -> Unit,
): Modifier {
    val queue = rememberQueueCoverDrag(state, onOpenQueue)
    return Modifier.playerCoverGestures(
        horizontal = false,
        vertical = enabled,
        onStart = {},
        onMove = { _, delta, displacement -> queue.move(delta, displacement) },
        onEnd = { _, displacement, velocity -> queue.finish(displacement, velocity) },
        onCancel = { _ -> queue.cancel() },
    )
}


@Composable
private fun PlayerLyricsHeaderBand(modifier: Modifier) {
    val reduce by com.convx.music.utils.rememberPreference(com.convx.music.constants.ReduceDynamicBlurKey, false)
    if (android.os.Build.VERSION.SDK_INT >= 33 && !reduce) {
        com.convx.music.ui.component.TopFadeBlur(
            hazeState = androidx.compose.runtime.remember { dev.chrisbanes.haze.HazeState() },
            pageColor = androidx.compose.ui.graphics.Color.Black,
            modifier = modifier,
            heightOverride = 48.dp,
            backdrop = com.convx.music.ui.component.LocalAppBackdrop.current,
            startFade = 0.dp,
        )
    } else {
        Box(
            modifier.fillMaxWidth().height(48.dp).background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.35f), androidx.compose.ui.graphics.Color.Transparent),
                ),
            ),
        )
    }
}
