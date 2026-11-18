package com.convx.music.ui.player

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

import android.animation.ValueAnimator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.convx.music.R
import com.convx.music.constants.CanvasMode
import com.convx.music.constants.ReduceDynamicBlurKey
import com.convx.music.playback.CanvasPhase
import com.convx.music.ui.component.shapes.ContinuousCapsule
import com.convx.music.ui.ios.component.IosButton
import com.convx.music.ui.ios.component.IosButtonStyle
import com.convx.music.ui.ios.component.IosText
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.delay

@Composable
internal fun CanvasFeedback(
    phase: CanvasPhase?,
    mode: CanvasMode,
    overrideEnabled: Boolean?,
    epoch: Long,
    onSetEnabled: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val reduceMotion by rememberPreference(ReduceDynamicBlurKey, defaultValue = false)
    val accessibility = LocalAccessibilityManager.current
    val shouldAnnounce = mode == CanvasMode.AUTO && phase == CanvasPhase.Showing && overrideEnabled == null
    var announced by remember(epoch) { mutableStateOf(false) }
    var visible by remember(epoch) { mutableStateOf(false) }
    LaunchedEffect(epoch, shouldAnnounce) {
        if (shouldAnnounce && !announced) {
            announced = true
            visible = true
            try {
                val timeout = accessibility?.calculateRecommendedTimeoutMillis(
                    originalTimeoutMillis = CanvasUndoMillis,
                    containsText = true,
                    containsControls = true,
                ) ?: CanvasUndoMillis
                delay(timeout)
            } finally {
                visible = false
            }
        }
    }

    key(epoch) {
        if (mode == CanvasMode.ASK && phase == CanvasPhase.Found && overrideEnabled == null) {
            IosButton(
                title = stringResource(R.string.player_canvas_show),
                onClick = { onSetEnabled(true) },
                modifier = modifier.sizeIn(minHeight = 48.dp),
                style = IosButtonStyle.BorderedProminent,
            )
        } else if (shouldAnnounce) {
            val animate = !reduceMotion && ValueAnimator.areAnimatorsEnabled()
            AnimatedVisibility(
                visible = visible,
                modifier = modifier,
                enter = if (animate) fadeIn(tween(150)) else EnterTransition.None,
                exit = if (animate) fadeOut(tween(120)) else ExitTransition.None,
            ) {
                Row(
                    modifier = Modifier
                        .background(IosTheme.colors.secondarySystemGroupedBackground, ContinuousCapsule())
                        .padding(start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    IosText(
                        text = stringResource(R.string.player_canvas_on),
                        color = IosTheme.colors.label,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .semantics { liveRegion = LiveRegionMode.Polite },
                    )
                    IosButton(
                        title = stringResource(R.string.undo),
                        onClick = {
                            visible = false
                            onSetEnabled(false)
                        },
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                        enabled = visible,
                        style = IosButtonStyle.Borderless,
                    )
                }
            }
        }
    }
}

private const val CanvasUndoMillis = 5_000L


@Composable
internal fun playerCanvasMenuItem(
    mediaId: String,
    onDismiss: () -> Unit,
): com.convx.music.ui.component.IosMenuItemData? {
    val connection = com.convx.music.LocalPlayerConnection.current ?: return null
    val track by connection.canvasTrack.collectAsStateWithLifecycle()
    if (track.mediaId != mediaId) return null
    val legacy by com.convx.music.utils.rememberPreference(com.convx.music.constants.CanvasThumbnailAnimationKey, true)
    val (mode) = com.convx.music.utils.rememberEnumPreference(
        com.convx.music.constants.CanvasModeKey, if (legacy) CanvasMode.AUTO else CanvasMode.OFF,
    )
    val saver by com.convx.music.utils.rememberPreference(com.convx.music.constants.DataSaverEnabledKey, false)
    if (mode == CanvasMode.OFF || saver) return null

    val dao = com.convx.music.LocalDatabase.current.get().canvasDecisionDao
    val choiceFlow = remember(dao, mediaId) { dao.observe(mediaId) }
    val choice by choiceFlow.collectAsStateWithLifecycle(initialValue = null)
    val checked = choice?.takeIf { it.mediaId == mediaId }?.enabled ?: (mode == CanvasMode.AUTO)
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    return com.convx.music.ui.component.IosMenuItemData(
        title = { IosText(stringResource(if (checked) R.string.player_canvas_disable else R.string.player_canvas_enable)) },
        onClick = {
            if (connection.canvasTrack.value != track) {
                onDismiss()
            } else {
                scope.launch {
                    dao.upsert(com.convx.music.db.entities.CanvasDecisionEntity(mediaId, !checked))
                    onDismiss()
                }
            }
        },
    )
}
