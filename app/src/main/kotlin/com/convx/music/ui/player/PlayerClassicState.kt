/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.MutableFloatState
import com.convx.music.playback.PlayerConnection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

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
