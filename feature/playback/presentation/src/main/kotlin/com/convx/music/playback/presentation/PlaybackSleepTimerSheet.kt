@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class)

package com.convx.music.playback.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.convx.music.playback.PlaybackSleepTimer
import com.convx.music.playback.PlaybackSnapshot
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.delay
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults.filledButtonColors
import zone.ien.hig.CupertinoButtonDefaults.plainButtonColors
import zone.ien.hig.CupertinoModalBottomSheet
import zone.ien.hig.CupertinoSlider
import zone.ien.hig.CupertinoText
import zone.ien.hig.PresentationDetent
import zone.ien.hig.PresentationStyle
import zone.ien.hig.rememberCupertinoSheetState
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme

@Composable
fun PlaybackSleepTimerSheet(
    timer: PlaybackSleepTimer,
    playback: PlaybackSnapshot,
    darkTheme: Boolean,
    onDismissRequest: () -> Unit,
) {
    val timerState by timer.state.collectAsState()
    val latestPlayback by rememberUpdatedState(playback)
    val initialMinutes = remember(timer, timerState.triggerTimeMs, timerState.pauseWhenTrackEnds) {
        timer.remainingMillis()?.let {
            ((it / MILLIS_PER_MINUTE).toFloat() / TIMER_STEP_MINUTES).roundToInt() * TIMER_STEP_MINUTES
        }
            ?.coerceIn(MIN_TIMER_MINUTES, MAX_TIMER_MINUTES)
            ?: DEFAULT_TIMER_MINUTES
    }
    var selectedMinutes by remember(timer, timerState.triggerTimeMs, timerState.pauseWhenTrackEnds) {
        mutableFloatStateOf(initialMinutes)
    }
    var remainingMillis by remember(timer) { mutableLongStateOf(0L) }
    val sheetState = rememberCupertinoSheetState(
        presentationStyle = PresentationStyle.Modal(
            detents = setOf(PresentationDetent.Medium, PresentationDetent.Large),
        ),
    )
    val sliderBackdrop = rememberLayerBackdrop()

    LaunchedEffect(sheetState) { sheetState.show() }
    LaunchedEffect(timer, timerState.triggerTimeMs, timerState.pauseWhenTrackEnds) {
        while (timerState.isActive) {
            remainingMillis = if (timerState.pauseWhenTrackEnds) {
                val current = latestPlayback
                ((current.durationMs ?: current.positionMs) - current.positionMs).coerceAtLeast(0L)
            } else {
                timer.remainingMillis() ?: 0L
            }
            delay(1_000L)
        }
        remainingMillis = 0L
    }

    CupertinoTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
        CupertinoModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = CupertinoTheme.colorScheme.secondarySystemGroupedBackground,
            contentColor = CupertinoTheme.colorScheme.label,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CupertinoText("Sleep timer", fontSize = 21.sp)
                    CupertinoButton(
                        onClick = onDismissRequest,
                        colors = plainButtonColors(),
                    ) {
                        CupertinoText("Done")
                    }
                }

                if (timerState.isActive) {
                    val timerDescription = when {
                        timerState.pauseWhenTrackEnds -> "Pauses when this track ends"
                        else -> "Pauses in ${formatRemainingTime(remainingMillis)}"
                    }
                    CupertinoText(timerDescription, color = CupertinoTheme.colorScheme.secondaryLabel)
                    CupertinoButton(
                        onClick = timer::clear,
                        colors = plainButtonColors(contentColor = Color(0xFFFF3B30)),
                    ) {
                        CupertinoText("Stop timer")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CupertinoText("Pause after", color = CupertinoTheme.colorScheme.secondaryLabel)
                    CupertinoText("${selectedMinutes.toInt()} minutes", fontSize = 30.sp)
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CupertinoTheme.colorScheme.tertiarySystemGroupedBackground)
                            .layerBackdrop(sliderBackdrop)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    ) {
                        CupertinoSlider(
                            value = selectedMinutes,
                            onValueChange = { selectedMinutes = it },
                            modifier = Modifier.fillMaxWidth(),
                            valueRange = MIN_TIMER_MINUTES..MAX_TIMER_MINUTES,
                            steps = TIMER_STEP_COUNT,
                            backdrop = sliderBackdrop,
                        )
                    }
                }

                CupertinoButton(
                    onClick = {
                        timer.start(selectedMinutes.toInt())
                        onDismissRequest()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = filledButtonColors(),
                ) {
                    CupertinoText("Start timer")
                }
                CupertinoButton(
                    onClick = {
                        timer.start(-1)
                        onDismissRequest()
                    },
                    enabled = playback.track != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = plainButtonColors(),
                ) {
                    CupertinoText("At the end of this track")
                }
            }
        }
    }
}

private fun formatRemainingTime(milliseconds: Long): String {
    val totalSeconds = milliseconds.coerceAtLeast(0L) / 1_000L
    val hours = totalSeconds / 3_600L
    val minutes = (totalSeconds % 3_600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0L) {
        "$hours:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        "$minutes:${seconds.toString().padStart(2, '0')}"
    }
}

private const val MILLIS_PER_MINUTE = 60_000L
private const val MIN_TIMER_MINUTES = 5f
private const val MAX_TIMER_MINUTES = 120f
private const val DEFAULT_TIMER_MINUTES = 30f
private const val TIMER_STEP_MINUTES = 5f
private const val TIMER_STEP_COUNT = 22
